package com.lec.spring.config;

import com.lec.spring.domain.User;
import com.lec.spring.repository.UserRepository;
import com.lec.spring.service.UserService;
import com.lec.spring.service.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;


public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public CustomLoginSuccessHandler(String defaultTargetUrl, UserService userService) {
        setDefaultTargetUrl(defaultTargetUrl);
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        System.out.println("🍤 인증 성공: onAuthenticationSuccess() 호출");

        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();

        if(userDetails.getAttributes() != null && userDetails.getUser().getJuminNo().equals("0".repeat(13))){
            System.out.println("OAuth 회원가입 진행중. 추가 정보 페이지로 redirect 합니다");
            response.sendRedirect("/user/register");
        }

        System.out.println("""
                    username: %s
                    password: %s
                    authorities: %s
                """.formatted(
//                        getClientIp(request),
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities())
        );


        LocalDateTime loginTime = LocalDateTime.now();
        System.out.println("\t로그인시간: " + loginTime);


        //사용자의 login 이력 저장
       User user = userService.findByUsername(userDetails.getUsername());
       int cnt = userService.saveUserLoginHistory(user.getId());
       if(cnt > 0) {
           System.out.println("\t" + user.getUsername() + " 로그인 이력 저장");
       }

//        request.getSession().setAttribute("loginTime", loginTime);

        //로그인 직전 url 로 redirect 함
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
