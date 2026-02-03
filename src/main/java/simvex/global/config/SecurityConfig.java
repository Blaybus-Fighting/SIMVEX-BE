package simvex.global.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import simvex.global.auth.oauth2.handler.OAuth2FailureHandler;
import simvex.global.auth.oauth2.handler.OAuth2SuccessHandler;
import simvex.global.auth.jwt.JWTUtil;
import simvex.global.auth.jwt.JWTFilter;
import simvex.global.auth.oauth2.service.PrincipalOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler,
                          OAuth2FailureHandler oAuth2FailureHandler,
                          JWTUtil jwtUtil) {
        this.principalOAuth2UserService = principalOAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http
                .csrf((auth) -> auth.disable())

                //From 로그인 방식 disable
                .formLogin((auth) -> auth.disable())

                //HTTP Basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())

                //JWTFilter 추가
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                //oauth2
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(principalOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler))


                //경로별 인가 작업
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/oauth/success",
                                "/login/**",
                                "/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                //세션 설정 : STATELESS
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //cors 설정
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);

                    configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                    return configuration;
                }
            }));

        return http.build();
    }
}
