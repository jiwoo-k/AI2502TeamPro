package com.lec.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.domain.User;
import com.lec.spring.domain.oauth.KakaoOAuthToken;
import com.lec.spring.domain.oauth.KakaoProfile;
import com.lec.spring.service.UserService;
import com.lec.spring.util.U;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    // kakao 로그인
    @Value("${app.oauth2.kakao.client-id}")
    private String kakaoClientId;

    @Value("${app.oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${app.oauth2.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${app.oauth2.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${app.oauth2.password}")
    private String oauthPassword;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public OAuth2Controller(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping("/kakao/callback")
    public String kakaoCallback(String code) {
        System.out.println("kakao 코드 받아오기 성공: " + code);

        //1. 가져온 코드값으로 엑세스 토큰 받기
        KakaoOAuthToken token = kakaoAccessToken(code);

        //2. 엑세스 토큰으로 사용자 정보 접근
        KakaoProfile profile = kakaoUserInfo(token.getAccess_token());

        //3. 회원가입시키기
        User user = registerKakaoUser(profile);

        //4. 로그인 처리
        loginKakaoUser(user);

        return "redirect:/";
    }

    private void loginKakaoUser(User kakaoUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), oauthPassword);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        U.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
        System.out.println("🎆Kakao 인증 로그인 처리 완료");
    }


    private User registerKakaoUser(KakaoProfile profile) {
        String provider = "KAKAO";
        String providerId = "" + profile.getId();
        String username = provider + "_" + providerId;
        String name = profile.getKakaoAccount().getProfile().getNickname();
        String password = oauthPassword;

        System.out.println("""
                🍻[카카오 인증 회원 정보]
                username: %s
                name: %s
                password: %s
                provider: %s
                providerId: %s
                """.formatted(username, name, password, provider, providerId));


        // 이미 가입한 회원인지, 혹은 비가입자인지 체크하여야 한다
        User user = userService.findByUsername(username);
        if(user == null){
            User newUser = User.builder()
                    .username(username)
                    .name(name)
                    .password(password)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            int cnt = userService.register(newUser);
            if(cnt > 0){
                System.out.println("🍻[Kakao 인증 회원가입 성공]");
                user = userService.findByUsername(username);
            }
            else{
                System.out.println("🍻[Kakao 인증 회원가입 실패]");
            }
        }
        else{
            System.out.println("🍻[Kakao] 인증. 이미 가입된 회원입니다");
        }

        return user;
    }

    private KakaoProfile kakaoUserInfo(String token) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(kakaoUserInfoUri,
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class);

        System.out.println("🎈카카오 사용자 profile 요청 응답: " + response);
        System.out.println("🎈카카오 사용자 profile 요청 응답 body: " + response.getBody());

        // 사용자 정보(JSON) -> Java 로 받아내기
        ObjectMapper mapper = new ObjectMapper();
        KakaoProfile profile = null;

        try {
            profile = mapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("""
                🎨[카카오 회원정보]
                id: %s
                nickname: %s
                """.formatted(profile.getId(), profile.getKakaoAccount().getProfile().getNickname()));

        return profile;
    }

    private KakaoOAuthToken kakaoAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        // header 준비 (HttpHeader)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // body 데이터 준비 (HttpBody)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        // 위 Header 와 Body 를 담은 HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 요청!
        ResponseEntity<String> response = rt.exchange(
                kakaoTokenUri,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        System.out.println("🐷카카오 AccessToken 요청 응답: " + response);
        //body 만 확인해보기
        System.out.println("🎨카카오 AccessToken 응답 body: " + response.getBody());

        //Json -> Java Object
        ObjectMapper mapper = new ObjectMapper();
        KakaoOAuthToken token = null;

        try {
            token = mapper.readValue(response.getBody(), KakaoOAuthToken.class);
            //AccessToken 확인
            System.out.println("🎃카카오 AccessToken: " + token.getAccess_token());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return token;
    }
}
