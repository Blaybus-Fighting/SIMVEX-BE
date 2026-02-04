package simvex.global.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;
import simvex.domain.user.dto.JwtPayload;
import simvex.domain.user.dto.UserDTO;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
import simvex.global.exception.ErrorCode;


@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        // 토큰이 없으면 다음 필터로 (인증이 필요한 경로는 authorizeHttpRequests에서 걸러짐)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 유효성 검증
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtPayload payload = jwtUtil.parseAndValidate(token);
            UserDTO userDTO = new UserDTO(
                    payload.id(),
                    payload.providerUserId(),
                    payload.name(),
                    payload.email(),
                    payload.role()
            );

            //UserDetails에 회원 정보 객체 담기
            PrincipalOAuth2User principalOAuth2User = new PrincipalOAuth2User(userDTO);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authentication  = new UsernamePasswordAuthenticationToken(principalOAuth2User, null, principalOAuth2User.getAuthorities());
            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT invalid: {}", e.getMessage());
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("JWT processing failed", e);
            request.setAttribute("exception", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // 1. Authorization 헤더 확인
        String header = request.getHeader("Authorization");
        if (header != null) {
            if (header.startsWith("Bearer ")) {
                return header.substring(7);
            }
            return header; // Bearer 없이 토큰만 보낸 경우 대비
        }

        // 2. 헤더에 없다면 쿠키에서 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> "Authorization".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/login") || path.startsWith("/oauth2") || path.equals("/favicon.ico");
    }
}