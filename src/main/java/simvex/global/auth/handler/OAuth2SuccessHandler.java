package simvex.global.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import simvex.global.auth.jwt.JWTUtil;
import simvex.global.auth.ticket.AuthTicketService;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${spring.jwt.access-token-expiration-ms}")
    private long tokenExpireMs;

    @Value("${login.success.redirect-uri}")
    private String frontendUrl;

    private final JWTUtil jwtUtil;
    private final AuthTicketService authTicketService;


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

        log.info("Authentication Success - redirect://{}", frontendUrl);

        String ticket = authTicketService.issue(token);
        response.sendRedirect(frontendUrl + "?ticket=" + ticket);
    }
}
