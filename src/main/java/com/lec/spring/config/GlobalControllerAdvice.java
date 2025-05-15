package com.lec.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("showHeader")
    public boolean showHeader(HttpServletRequest request) {
        // 로그인·회원가입 페이지(/user/login, /user/register)에서는 false,
        // 그 외에는 true
        String uri = request.getRequestURI();
        return !uri.matches("/user/(login|register).*");
    }

}
