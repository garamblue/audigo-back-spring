package com.audigo.audigo_back.security;

import com.audigo.audigo_back.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 검증 필터
 * 모든 요청에 대해 JWT 토큰을 검증하고 SecurityContext에 인증 정보 설정
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);

            if (token != null) {
                // 회원 토큰 검증 시도
                Claims memberClaims = jwtUtil.verifyMemberAccessToken(token);

                if (memberClaims != null) {
                    // 회원 인증 성공
                    setMemberAuthentication(request, memberClaims);
                } else {
                    // 관리자 토큰 검증 시도
                    Claims adminClaims = jwtUtil.verifyAdminToken(token);

                    if (adminClaims != null) {
                        // 관리자 인증 성공
                        setAdminAuthentication(request, adminClaims);
                    }
                }
            }

        } catch (Exception e) {
            log.error("JWT authentication error", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 회원 인증 정보 설정
     */
    private void setMemberAuthentication(HttpServletRequest request, Claims claims) {
        Map<String, Object> principal = new HashMap<>();
        principal.put("type", "MEMBER");
        principal.put("sns_val", claims.get("sns_val"));
        principal.put("nickname", claims.get("nickname"));
        principal.put("push_tkn", claims.get("push_tkn"));

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
            );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Member authenticated: {}", claims.get("nickname"));
    }

    /**
     * 관리자 인증 정보 설정
     */
    private void setAdminAuthentication(HttpServletRequest request, Claims claims) {
        Map<String, Object> principal = new HashMap<>();
        principal.put("type", "ADMIN");
        principal.put("id", claims.get("id"));
        principal.put("deviceId", claims.get("deviceId"));
        principal.put("IP", claims.get("IP"));

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Admin authenticated: {}", claims.get("id"));
    }
}
