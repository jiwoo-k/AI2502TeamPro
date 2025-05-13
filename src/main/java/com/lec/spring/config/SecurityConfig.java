package com.lec.spring.config;

import com.lec.spring.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // UserDetailsService 구현체
    private final UserDetailsServiceImpl userDetailService;

    // 생성자 주입
    public SecurityConfig(UserDetailsServiceImpl userDetailService) {
        this.userDetailService = userDetailService;
    }

    // PasswordEncoder Bean 등록
    // 사용자의 비밀번호를 안전하게 암호화하고 검증하기 위해 필요
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 공급자(Authentication Provider) Bean 등록
    // 사용자 정보를 로드하는 UserDetailsService 와 비밀번호 인코더를 연결
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService); // 여러분이 만든 UserDetailsService 구현체 설정
        provider.setPasswordEncoder(passwordEncoder()); // 위에서 등록한 PasswordEncoder 설정
        // provider.setHideUserNotFoundExceptions(false); // 개발 단계에서 사용자 없는 경우 메시지 확인용 (운영 시에는 true 권장)
        return provider;
    }

    // Security Filter Chain Bean 등록
    // HTTP 요청에 대한 보안 규칙을 설정
    // 어떤 URL 에 누가 접근할 수 있는지, 로그인/로그아웃 설정 등을 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // HTTP 요청에 대한 접근 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        // permitAll(): 모든 사용자 접근 허용 (인증되지 않은 사용자 포함)
                        .requestMatchers(
                                "/", "/home", "/user/login", "/user/register", // 기본 페이지 및 사용자 관련 페이지
                                "/static/**", "/css/**", "/js/**", "/images/**", "/upload/**" // 정적 리소스
                        ).permitAll()

                        // hasRole(): 특정 권한을 가진 사용자만 접근 허용 (DB에는 "ROLE_ADMIN" 형태로 저장될 권한 이름 사용)
                        .requestMatchers("/admin/**").hasRole("ADMIN") // "/admin" 으로 시작하는 모든 경로는 "ADMIN" 권한 필요

                        // anyRequest(): 위에서 설정하지 않은 나머지 모든 요청
                        .anyRequest().authenticated() // 인증(로그인)된 사용자만 접근 허용
                )

                // 폼 기반 로그인 설정
                .formLogin(formLogin -> formLogin
                        .loginPage("/user/login") // 커스텀 로그인 페이지 URL 설정 (GET 요청)
                        .loginProcessingUrl("/user/login") // 로그인 폼 submit 받을 URL (POST 요청)
                        .defaultSuccessUrl("/home", true) // 로그인 성공 후 이동할 기본 페이지 URL
                        .failureUrl("/user/login?error") // 로그인 실패 후 이동할 페이지 URL (에러 쿼리 파라미터 포함)
                        .usernameParameter("username") // 로그인 폼에서 사용할 사용자 이름 필드 이름 (기본값은 username)
                        .passwordParameter("password") // 로그인 폼에서 사용할 비밀번호 필드 이름 (기본값은 password)
                        .permitAll() // 로그인 페이지 관련 URL 들은 모든 사용자 접근 허용
                )

                // OAuth2 로그인 설정 (build.gradle 에 spring-boot-starter-oauth2-client 가 있다면 필요)
                .oauth2Login(oauth2Login -> oauth2Login
                                .loginPage("/user/login") // OAuth2 로그인 시작 페이지 (보통 일반 로그인 페이지와 동일하게 설정)
                        // .defaultSuccessUrl("/home", true) // OAuth2 로그인 성공 후 이동할 기본 페이지 URL
                        // .failureUrl("/user/login?oauth2_error") // OAuth2 로그인 실패 후 이동할 페이지 URL
                        // .userInfoEndpoint(userInfo -> userInfo
                        //     .userService(customOAuth2UserService) // 필요 시 커스텀 UserService 설정
                        // )
                        // .successHandler(customOAuth2AuthenticationSuccessHandler) // 필요 시 커스텀 SuccessHandler 설정
                )


                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/user/logout") // 로그아웃 처리할 URL (기본값 /logout)
                        // Controller 에는 이 URL 에 대한 특별한 핸들러 메소드가 없어도 ehla
                        // Spring Security 가 이 URL 로 들어오는 요청을 가로채서 로그아웃 처리
                        .logoutSuccessUrl("/home") // 로그아웃 성공 후 이동할 페이지 URL
                        .invalidateHttpSession(true) // HTTP 세션 무효화 (로그아웃 시)
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제 (로그아웃 시)
                        .permitAll() // 로그아웃 관련 URL 은 모든 사용자 접근 허용
                );



        return http.build(); // 설정된 HttpSecurity 객체를 바탕으로 SecurityFilterChain 객체 생성
    }

}







