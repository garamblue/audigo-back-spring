package com.audigo.audigo_back.controller.admin;

import com.audigo.audigo_back.service.admin.AdminLoginService;
import com.audigo.audigo_back.util.AesUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

/**
 * 관리자 로그인/로그아웃 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminLoginController {

    private final AdminLoginService adminLoginService;
    private final AesUtil aesUtil;

    /**
     * 관리자 로그인
     * POST /api/admin/auth/signin
     *
     * Request: {"data": "encrypted_data"}
     * Decrypted: {"id": "admin_id", "pw": "password"}
     */
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String encryptedData = request.get("data");

            if (encryptedData == null || encryptedData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("msg", "Missing encrypted data"));
            }

            // 복호화
            Map<String, Object> data = aesUtil.decryptAdmin(encryptedData, Map.class);
            String encryptedId = (String) data.get("id");
            String encryptedPw = (String) data.get("pw");
            String deviceId = (String) data.getOrDefault("deviceId", "unknown");

            // IP 및 User-Agent 추출
            String ipAddr = getClientIP(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            // 로그인 처리
            Map<String, Object> result = adminLoginService.signIn(
                    encryptedId, encryptedPw, deviceId, ipAddr, userAgent);

            // 응답 암호화
            String encryptedResponse = aesUtil.encryptAdmin(result);

            return ResponseEntity.ok(Map.of("data", encryptedResponse));

        } catch (Exception e) {
            log.error("로그인 오류", e);
            Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "로그인 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptAdmin(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }

    /**
     * 관리자 로그아웃
     * POST /api/admin/auth/signout
     */
    @PostMapping("/signout")
    public ResponseEntity<Map<String, Object>> signOut(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            // 토큰 추출
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("msg", "Invalid token"));
            }

            String encryptedData = request.get("data");
            Map<String, Object> data = aesUtil.decryptAdmin(encryptedData, Map.class);
            BigInteger aIdx = new BigInteger(data.get("aIdx").toString());

            // 로그아웃 처리
            adminLoginService.signOut(token, aIdx);

            Map<String, Object> response = Map.of(
                    "code", "1",
                    "msg", "로그아웃 성공"
            );
            String encryptedResponse = aesUtil.encryptAdmin(response);

            return ResponseEntity.ok(Map.of("data", encryptedResponse));

        } catch (Exception e) {
            log.error("로그아웃 오류", e);
            Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "로그아웃 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptAdmin(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 클라이언트 IP 추출
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 여러 IP가 있을 경우 첫 번째 IP 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
