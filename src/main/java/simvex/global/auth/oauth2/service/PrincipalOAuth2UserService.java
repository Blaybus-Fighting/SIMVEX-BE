package simvex.global.auth.oauth2.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
import simvex.global.auth.oauth2.dto.GoogleResponse;
import simvex.global.auth.oauth2.dto.OAuth2Response;
import simvex.domain.user.dto.UserDTO;
import simvex.domain.user.entity.User;
import simvex.domain.user.repository.UserRepository;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public PrincipalOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            throw new OAuth2AuthenticationException("Unsupported registrationId: " + registrationId);
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String providerUserId = oAuth2Response.getProviderId();
        User user  = userRepository.findByProviderUserId(providerUserId);

        if (user == null) {
            user = new User();
            user.setProviderUserId(providerUserId);
            user.setEmail(oAuth2Response.getEmail());
            user.setName(oAuth2Response.getName());
            user.setProfileImage(oAuth2Response.getProfileImage());
            user.setRole("ROLE_USER");

            userRepository.save(user);
        }
        else {
            user.setName(oAuth2Response.getName());
            user.setEmail(oAuth2Response.getEmail());
            String profileImage = oAuth2Response.getProfileImage();
            if (profileImage != null && !profileImage.isBlank()) {
                user.setProfileImage(profileImage);
            }

            userRepository.save(user);
        }

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getProviderUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getProfileImage()
        );
        return new PrincipalOAuth2User(userDTO, oAuth2User.getAttributes());
    }
}
