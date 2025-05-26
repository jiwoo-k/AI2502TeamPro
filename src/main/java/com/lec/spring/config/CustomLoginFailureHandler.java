package com.lec.spring.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;


public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
    private final String DEFAULT_FAILURE_FORWARD_URL = "/user/loginError";


    //인증 실패 직후 호출되는 콜백
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("🎨 로그인 실패: onAuthenticationFailure() 호출");
        System.out.println("🚫 전달된 예외 유형: " + exception.getClass().getName());
        System.out.println("🚫 예외 메시지: " + exception.getMessage());

        String errorMessage = null;

        if(exception instanceof BadCredentialsException) {
            errorMessage = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해 주십시오.";
        }
        else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = exception.getMessage();
        }
        else if(exception instanceof DisabledException) {
            errorMessage = exception.getMessage();
        }
        else if(exception instanceof LockedException){
            errorMessage = exception.getMessage();
        }
        //< expired the credential
        else if(exception instanceof CredentialsExpiredException) {
            errorMessage = "비밀번호 유효기간이 만료 되었습니다. 관리자에게 문의하세요.";
        }
        else {
            errorMessage = "알수 없는 이유로 로그인에 실패하였습니다. 관리자에게 문의하세요.";
        }


        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("username", request.getParameter("username"));

        //redirect 나 forward 시켜준다
        request.getRequestDispatcher(DEFAULT_FAILURE_FORWARD_URL).forward(request, response);
    }
}
