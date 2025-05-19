package com.lec.spring.config.oauth;

import com.lec.spring.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    @Value("${app.oauth2.password}")
    private String oauth2Password;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public PrincipalOauth2UserService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //확인용
        System.out.println("""
                🎡[loadUser() 호출]
                    ClientRegistration: %s
                    RegistrationId: %s
                    AccessToken: %s
                    OAuth2User Attributes : %s
                """.formatted(userRequest.getClientRegistration() //ClientRegistration
                , userRequest.getClientRegistration().getRegistrationId() //String
                , userRequest.getAccessToken().getTokenValue() //String
                , oAuth2User.getAttributes() //Map<String, Object> <- 사용자 프로필 정보가 담겨온다
        ));

        return oAuth2User;
    }
}
