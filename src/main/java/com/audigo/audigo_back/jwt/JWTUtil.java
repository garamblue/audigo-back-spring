package com.audigo.audigo_back.jwt;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 리액트 강의 8강 JwtProvider 를 대체
 */
@Slf4j
@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email",
                String.class);
    }

    public String getId(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id",
                String.class);
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username",
                String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",
                String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }

    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 일반 member 사용자 token 생성
     * JWT 토큰에 email 값으로 반환
     * @param email
     * @param expiredMs
     * @return
     */
    public String createJwtWithEmail(String email, Long expiredMs) {
        // 1. Date 객체 생성 (현재날짜)
        Date date = new Date(System.currentTimeMillis());
        // 2. Date -> LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // 3. LocalDateTime 출력
        log.info("create JWT WithEmail: =============== start");
        log.info("=== localDateTime: " + localDateTime);

        String token = Jwts.builder()
                .claim("email", email)
                .issuedAt(date)
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();

        log.info("=== created token: " + token);
        log.info("create JWT WithEmail: =============== end");

        return token;
    }

    /**
     * admin 사용자 token 생성
     * @param id
     * @param expiredMs
     * @return
     */
    public String createJwtWithId(String id, Long expiredMs) {
        // 1. Date 객체 생성 (현재날짜)
        Date date = new Date(System.currentTimeMillis());
        // 2. Date -> LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // 3. LocalDateTime 출력
        log.info("create JWT With Id: =============== start");
        log.info("=== localDateTime: " + localDateTime);

        String token = Jwts.builder()
                .claim("id", id)
                //.claim("role", role) //admin 권한세팅한 토큰 발행 시 String role 파라미터 추가하기
                .issuedAt(date)
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();

        log.info("=== created token: " + token);
        log.info("create JWT With Id: =============== end");

        return token;
    }
}
