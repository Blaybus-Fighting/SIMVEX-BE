package simvex.global.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import simvex.domain.user.dto.JwtPayload;
import simvex.domain.user.dto.UserDTO;
import simvex.global.auth.jwt.JWTUtil;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        // SSE(text/event-stream)의 경우에만 적용
        if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
            log.info("SSE Request 요청에 대한 인증 시작 : {}", request.getRequestURI());
            String token = JWTUtil.extractToken(request);

            JwtPayload payload = jwtUtil.parseAndValidate(token);
            Authentication authentication = getAuthentication(payload);
            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("SSE Request 요청 인증 완료");
        }

        filterChain.doFilter(request, response);
    }

    private static @NonNull Authentication getAuthentication(JwtPayload payload) {
        UserDTO userDTO = new UserDTO(
                payload.id(),
                payload.providerUserId(),
                payload.name(),
                payload.email(),
                payload.role(),
                payload.profileImage()
        );

        //UserDetails에 회원 정보 객체 담기
        PrincipalOAuth2User principalOAuth2User = new PrincipalOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        return new UsernamePasswordAuthenticationToken(
                principalOAuth2User,
                null,
                principalOAuth2User.getAuthorities()
        );
    }
}
