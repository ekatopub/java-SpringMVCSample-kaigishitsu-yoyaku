package com.example.demo.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
    	
    	//debug
    	System.out.println("CustomAuthenticationFailureHandler is called");

        // デフォルトのエラーメッセージをセッションから削除
        request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        // 独自のメッセージを処理するために、指定したURLにリダイレクト
        response.sendRedirect(request.getContextPath() + "/login?error=customError");
    }
}