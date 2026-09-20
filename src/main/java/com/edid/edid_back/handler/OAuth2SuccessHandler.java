package com.audigo.audigo_back.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.audigo.audigo_back.entity.CustomOAuth2Member;
import com.audigo.audigo_back.jwt.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
    
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2Member oAuth2Memb = (CustomOAuth2Member) authentication.getPrincipal();
        String userId = oAuth2Memb.getName();
        String token = jwtUtil.createJwtWithId(userId, 3600000L);

        response.sendRedirect("http://localhost:3000/auth/oauth-repsonse/" + token + "/3600");

    }
}
