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
import simvex.domain.user.entity.UserEntity;
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
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByUsername(username);

        if (existData == null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new PrincipalOAuth2User(userDTO);
        }
        else {

            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRole());

            return new PrincipalOAuth2User(userDTO);
        }
    }
}
