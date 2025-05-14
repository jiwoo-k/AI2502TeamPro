package com.lec.spring.config;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

    @Autowired
    SqlSession sqlSession;

    // UserDetailsService 구현체
    private final UserDetailsService userDetailService;

    // 생성자 주입
    public SecurityConfig(UserDetailsService userDetailService) {
        this.userDetailService = userDetailService;
    }


    // 인증 공급자(Authentication Provider) Bean 등록
    // 사용자 정보를 로드하는 UserDetailsService 와 비밀번호 인코더를 연결
    /*@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService); // 여러분이 만든 UserDetailsService 구현체 설정
        provider.setPasswordEncoder(passwordEncoder()); // 위에서 등록한 PasswordEncoder 설정
        // provider.setHideUserNotFoundExceptions(false); // 개발 단계에서 사용자 없는 경우 메시지 확인용 (운영 시에는 true 권장)
        return provider;
    }*/

    // Security Filter Chain Bean 등록
    // HTTP 요청에 대한 보안 규칙을 설정
    // 어떤 URL 에 누가 접근할 수 있는지, 로그인/로그아웃 설정 등을 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/user/login", "/user/register",
                                "/board/list", "/board/detail/**", "/css/**", "/js/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .defaultSuccessUrl("/")
                        .successHandler(new CustomLoginSuccessHandler("/home"))
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

                .build();
    }

}







