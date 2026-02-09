package simvex.global.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

//    private static final String FRONT_REDIRECT_URL = "http://localhost:3000/login"; // 로그인 페이지
    @Value("${login.success.redirect-uri}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        log.error("OAuth2 Failure: {}", exception.getMessage());
        log.error("OAuth2 Failure Redirect : {}", frontendUrl + "?error=fail");

        // 프론트엔드에서는 error=fail 조건일 때 "로그인에 실패했습니다."
        response.sendRedirect(frontendUrl + "?error=fail");
    }
}
