package simvex.global.auth.oauth2.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import simvex.domain.user.dto.UserDTO;

public class PrincipalOAuth2User implements OAuth2User {
    private final UserDTO userDTO;
    private final Map<String, Object> attributes;

    public PrincipalOAuth2User(UserDTO userDTO, Map<String, Object> attributes) {
        this.userDTO = userDTO;
        this.attributes = attributes;
    }

    // JWT 인증용(속성 없음)
    public PrincipalOAuth2User(UserDTO userDTO) {
        this(userDTO, Map.of());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userDTO.role()));
    }

    @Override
    public String getName() {
        return userDTO.name();
    }

    public String getEmail() {
        return userDTO.email();
    }

    public Long getId() {
        return userDTO.id();
    }

    public String getProviderUserId() {
        return userDTO.providerUserId();
    }
}
