package simvex.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import simvex.domain.user.dto.JwtPayload;

@Component
public class JWTUtil {
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EMAIL = "email";

    private static final String BEARER = "Bearer ";

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public JwtPayload parseAndValidate(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date exp = claims.getExpiration();
        if (exp == null || exp.before(new Date())) {
            throw new JwtException("Token expired");
        }

        String providerUserId = claims.getSubject();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new JwtException("Missing subject(providerUserId)");
        }

        Long id = claims.get(CLAIM_ID, Long.class);
        String name = claims.get(CLAIM_NAME, String.class);
        String email = claims.get(CLAIM_EMAIL, String.class);
        String role = claims.get(CLAIM_ROLE, String.class);

        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }

        return new JwtPayload(id, providerUserId, name, email, role);

    }


    public String createJwt(String providerUserId, Long id, String name, String email, String role, Long expiredMs) {
        return Jwts.builder()
                .subject(providerUserId)
                .claim(CLAIM_ID, id)
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public static String extractToken(HttpServletRequest request) {
        // 1. Authorization 헤더 확인
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            if (header.startsWith(BEARER)) {
                return header.substring(BEARER.length());
            }
            return header; // Bearer 없이 토큰만 보낸 경우 대비
        }

        // 2. 헤더에 없다면 쿠키에서 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> HttpHeaders.AUTHORIZATION.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}