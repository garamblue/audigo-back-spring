package com.audigo.audigo_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audigo.audigo_back.service.TokenService;
import com.audigo.audigo_back.service.TokenService.TokenResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenAuthController {
    
    private final TokenService tokenService;
    
    /**
     * 로그인 (예시)
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        // 실제로는 사용자 인증 로직이 필요
        // 여기서는 예시로 바로 토큰 생성
        TokenResponse tokens = tokenService.generateTokens(request.getUserId());
        return ResponseEntity.ok(tokens);
    }
    
    /**
     * Access Token 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            TokenResponse tokens = tokenService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        tokenService.logout(request.getUserId());
        return ResponseEntity.ok().build();
    }
    
    // Request DTOs
    public static class LoginRequest {
        private String userId;
        private String password;
        
        public String getUserId() { return userId; }
        public String getPassword() { return password; }
    }
    
    public static class RefreshRequest {
        private String refreshToken;
        
        public String getRefreshToken() { return refreshToken; }
    }
    
    public static class LogoutRequest {
        private String userId;
        
        public String getUserId() { return userId; }
    }
}