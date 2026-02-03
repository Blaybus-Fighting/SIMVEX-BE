package simvex.global.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

//    private static final String FRONT_REDIRECT_URL = "http://localhost:3000/login"; // 로그인 페이지
    private static final String FRONT_REDIRECT_URL = "http://localhost:8080/oauth/success"; // fail

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        log.error("OAuth2 Failure: " + exception.getMessage());

        // 기존 인증 쿠키 제거
        response.addCookie(expireCookie("Authorization"));

        // 프론트엔드에서는 error=fail 조건일 때 "로그인에 실패했습니다."
        response.sendRedirect(FRONT_REDIRECT_URL + "?error=fail");
    }

    private Cookie expireCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
