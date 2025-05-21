package com.lec.spring.config;

import com.lec.spring.config.oauth.PrincipalOauth2UserService;
import com.lec.spring.service.UserService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // OAuth 로그인
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    private UserService userService;

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // Security Filter Chain Bean 등록
    // HTTP 요청에 대한 보안 규칙을 설정
    // 어떤 URL 에 누가 접근할 수 있는지, 로그인/로그아웃 설정 등을 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/mypage/**",
                                "/board/write",
                                "/board/update/**",
                                "board/follow/**",
                                "board/warning/**"
                        ).authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .defaultSuccessUrl("/")
                        .successHandler(new CustomLoginSuccessHandler("/home", userService))
                        .failureHandler(new CustomLoginFailureHandler())
                )
                .logout(httpSecurity -> httpSecurity
                                .logoutUrl("/user/logout")
//                                .invalidateHttpSession(false) //session invalidate (default true)
                                .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                )
                .exceptionHandling(httpsecurity -> httpsecurity
                        // 권한(Authorization) 오류 발생시 수행할 코드
                        // .accessDeniedHandler(AccessDeniedHandler)
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .oauth2Login(httpSecurity -> httpSecurity
                        .loginPage("/user/login") // 로그인 페이지를 동일한 url 로 지정

                        // code 를 받아오는 것이 아니라 "AccessToken" 과 사용자 "프로필 정보" 를 한번에 받아온다(편리)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauth2UserService) //userService(Oauth2UserService<Oauth2UserRequest, Oauth2User>)
                        )
                        .successHandler(new CustomLoginSuccessHandler("/home", userService)
                        ));

        return http.build();
    }

}







