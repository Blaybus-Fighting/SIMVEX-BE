package simvex.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import simvex.domain.user.dto.JwtPayload;

@Component
public class JWTUtil {
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EMAIL = "email";

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

        String name = claims.get(CLAIM_NAME, String.class);
        String email = claims.get(CLAIM_EMAIL, String.class);
        String role = claims.get(CLAIM_ROLE, String.class);

        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }

        return new JwtPayload(providerUserId, name, email, role);

    }


    public String createJwt(String providerUserId, String name, String email, String role, Long expiredMs) {
        return Jwts.builder()
                .subject(providerUserId)
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}