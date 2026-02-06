package simvex.global.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import simvex.global.auth.jwt.JWTUtil;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${spring.jwt.access-token-expiration-ms}")
    private long tokenExpireMs;
    private final JWTUtil jwtUtil;
@Value("${app.frontend-url:/}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        //OAuth2User
        PrincipalOAuth2User principal  = (PrincipalOAuth2User) authentication.getPrincipal();

        Long id = principal.getId();
        String providerUserId  = principal.getProviderUserId();
        String name = principal.getName();
        String email = principal.getEmail();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(providerUserId, id, name, email, role, tokenExpireMs);

        response.addCookie(createCookie("Authorization", token, (int) (tokenExpireMs / 1000)));
        response.sendRedirect(frontendUrl);
    }

    private Cookie createCookie(String key, String value, int maxAge) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
