package com.audigo.audigo_back.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.admin.secret}")
    private String adminSecret;

    @Value("${jwt.admin.expiration:3600000}") // 기본 1시간
    private Long adminExpiration;

    @Value("${jwt.member.secret}")
    private String memberSecret;

    @Value("${jwt.member.expiration:3600000}") // 기본 1시간
    private Long memberExpiration;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.refresh.expiration:2592000000}") // 기본 30일
    private Long refreshExpiration;

    /**
     * 관리자 JWT 토큰 생성
     */
    public String generateAdminToken(Map<String, Object> payload) {
        SecretKey key = Keys.hmacShaKeyFor(adminSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + adminExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 회원 Access Token 생성
     */
    public String generateMemberAccessToken(Map<String, Object> payload) {
        SecretKey key = Keys.hmacShaKeyFor(memberSecret.getBytes(StandardCharsets.UTF_8));

        // exp, iat, nbf 필드 제거 (자동 생성됨)
        payload.remove("exp");
        payload.remove("iat");
        payload.remove("nbf");

        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + memberExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 회원 Refresh Token 생성
     */
    public String generateMemberRefreshToken(Map<String, Object> payload) {
        SecretKey key = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));

        // exp, iat, nbf 필드 제거 (자동 생성됨)
        payload.remove("exp");
        payload.remove("iat");
        payload.remove("nbf");

        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 관리자 토큰 검증
     */
    public Claims verifyAdminToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(adminSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException e) {
            log.error("Admin token verification failed", e);
            return null;
        }
    }

    /**
     * 회원 Access Token 검증
     */
    public Claims verifyMemberAccessToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(memberSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException e) {
            log.error("Member access token verification failed", e);
            return null;
        }
    }

    /**
     * 회원 Refresh Token 검증
     */
    public Claims verifyMemberRefreshToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException e) {
            log.error("Member refresh token verification failed", e);
            return null;
        }
    }

    /**
     * 토큰에서 특정 클레임 추출
     */
    public String getClaimValue(Claims claims, String key) {
        if (claims == null) {
            return null;
        }
        return claims.get(key, String.class);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
