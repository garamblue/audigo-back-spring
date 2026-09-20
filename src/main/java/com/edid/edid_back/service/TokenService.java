package com.audigo.audigo_back.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.audigo.audigo_back.entity.RefreshTokenEntity;
import com.audigo.audigo_back.jwt.JWTUtil;
import com.audigo.audigo_back.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    
    private long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private int REFRESH_TOKEN_EXPIRE_TIME = 2160; // 90일
    
    /**
     * Access Token과 Refresh Token 생성
     */
    @Transactional
    public TokenResponse generateTokens(String userId) {
        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);
        
        // 새 토큰 생성
        String accessToken = jwtUtil.createJwtWithId(userId, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = "abc123"; //jwtUtil.createRefreshToken(userId, REFRESH_TOKEN_EXPIRE_TIME);
        
        // Refresh Token DB 저장
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME);
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(userId, refreshToken, expiresAt);
        refreshTokenRepository.save(refreshTokenEntity);
        
        return new TokenResponse(accessToken, refreshToken, ACCESS_TOKEN_EXPIRE_TIME);
    }
    
    /**
     * Refresh Token으로 Access Token 갱신
     */
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        Optional<RefreshTokenEntity> tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        
        if (tokenEntity.isEmpty() || tokenEntity.get().isExpired()) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        
        // 새 Access Token 생성
        String userId = tokenEntity.get().getUserId();
        String newAccessToken = jwtUtil.createJwtWithId(userId, ACCESS_TOKEN_EXPIRE_TIME);
        
        return new TokenResponse(newAccessToken, refreshToken, ACCESS_TOKEN_EXPIRE_TIME);
    }
    
    /**
     * 로그아웃 - Refresh Token 삭제
     */
    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
    
    /**
     * Token Response DTO
     */
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private long expiresIn;
        
        public TokenResponse(String accessToken, String refreshToken, long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }
        
        // Getters
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public long getExpiresIn() { return expiresIn; }
    }
}