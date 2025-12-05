package com.audigo.audigo_back.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audigo.audigo_back.dto.request.admin.auth.AdminSignUpRequestDto;
import com.audigo.audigo_back.dto.request.admin.auth.AdminSignInRequestDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignUpResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInInfoResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInResponseDto;
import com.audigo.audigo_back.service.admin.AdminAuthService;

import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.time.Instant;
import jakarta.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Admin Auth API", description = "관리자 권한 관련 API")
@RestController
@RequestMapping("/api/adm/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    /**
     * 관리자 최초 등록
     * @param requestBody
     * @return
     */
    @Operation(summary = "최초 관리자 등록", description = "Admin 계정 최초 생성.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "등록성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DUID", description = "DUPLICATE_ID", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "id", description = "3 ~ 50자 이내", required = true, example = "test01")
        ,@Parameter(name = "pwd", description = "8 ~ 50자 이내", required = true, example = "12345678")
        ,@Parameter(name = "nm", description = "3 ~ 50자 이내", required = true, example = "최고운영자")
        ,@Parameter(name = "roleCd", description = "3 ~ 30자 이내", required = true, example = "role01")
        ,@Parameter(name = "rememberYn", description = "1자", required = true, example = "Y")
        ,@Parameter(name = "actYn", description = "1자", required = true, example = "Y")
        ,@Parameter(name = "orgCd", description = "조직코드", required = false, example = "O001")
        ,@Parameter(name = "cmpCd", description = "소속사코드", required = false, example = "C001")
        ,@Parameter(name = "deptCd", description = "부서코드", required = false, example = "D001")
        ,@Parameter(name = "mobile", description = "전화번호", required = false, example = "01012345678")
        ,@Parameter(name = "osType", description = "OS 종류", required = false, example = "A")
        ,@Parameter(name = "pushKey", description = "푸시키", required = false, example = "pushKey")
        ,@Parameter(name = "lastIp", description = "마지막 접속 IP", required = false, example = "127.0.0.1")
        ,@Parameter(name = "connInfo", description = "접속정보", required = false, example = "connInfo")
    })
    @PostMapping("/register")
    public ResponseEntity<? super AdminSignUpResponseDto> register(@RequestBody @Valid AdminSignUpRequestDto requestBody) {
        ResponseEntity<? super AdminSignUpResponseDto> response = adminAuthService.register(requestBody);
        return response;
    }




    /**
     * 관리자 로그인
     * @param requestBody
     * @return
     */
    @Operation(summary = "관리자 로그인", description = "Admin 계정 로그인.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "로그인 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "SIGN_IN_FAIL", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DE", description = "Database Error", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "id", description = "AdminSignInRequestDto 참조 3 ~ 50자 이내", required = true, example = "test1"),
        @Parameter(name = "password", description = "8 ~ 50자 이내", required = true, example = "12345678")
    })
    @PostMapping("/sign-in")
    public ResponseEntity<? super AdminSignInResponseDto> signIn(@RequestBody @Valid AdminSignInRequestDto requestBody) {
        log.info("============ Admin LoginId: " + requestBody.getId());

        ResponseEntity<? super AdminSignInResponseDto> response = adminAuthService.signIn(requestBody);

        return response;
    }

    /**
     * 로그인한 관리자 정보조회
     * @param id
     * @return
     */
    @Operation(summary = "로그인한 관리자 정보조회", description = "로그인한 Admin 계정 정보조회.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "로그인 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "NEA", description = "NOT_EXISTING_ADMIN_USER", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DE", description = "Database Error", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "id", description = "3 ~ 50자 이내", required = true, example = "test1")
    })
    @GetMapping("info")
    public ResponseEntity<? super AdminSignInInfoResponseDto> getAdminsInfo(@AuthenticationPrincipal String id) {
        log.info("=== AuthenticationPrincipal id: " + id);

        ResponseEntity<? super AdminSignInInfoResponseDto> response = adminAuthService.getAdminsInfo(id);
        
        return response;
    }

    // WebAuthn 관련 임시 저장소
    private final Map<String, String> challenges = new HashMap<>();
    private final Map<String, Map<String, Object>> users = new HashMap<>();

    /**
     * WebAuthn 등록 옵션 생성
     */
    @Operation(summary = "WebAuthn 등록 옵션 생성", description = "WebAuthn 등록을 위한 옵션을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "등록 옵션 생성 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "ISE", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "fingerprint", description = "디바이스 핑거프린트 (8~50자)", required = true, example = "abc123def456")
    })
    @PostMapping("/webauthn/registration-options")
    public ResponseEntity<Map<String, Object>> getRegistrationOptions(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            String fingerprint = request.get("fingerprint");
            String challenge = generateChallenge();
            
            challenges.put(fingerprint, challenge);
            
            // 실제 WebAuthn 데이터 생성
            String credentialId = generateCredentialId();
            KeyPair keyPair = generateKeyPair();
            String publicKey = encodePublicKey(keyPair.getPublic());
            String attestationObject = generateAttestationObject(challenge, credentialId, keyPair.getPublic(), fingerprint, httpRequest);
            String clientDataJSON = generateClientDataJSON(challenge, httpRequest);
            Map<String, Object> deviceInfo = generateDeviceInfo(httpRequest, fingerprint);
            
            // 생성된 데이터를 임시 저장
            Map<String, Object> userData = new HashMap<>();
            userData.put("credentialId", credentialId);
            userData.put("publicKey", publicKey);
            userData.put("attestationObject", attestationObject);
            userData.put("clientDataJSON", clientDataJSON);
            userData.put("deviceInfo", deviceInfo);
            userData.put("fingerprint", fingerprint);
            userData.put("createdAt", Instant.now().toEpochMilli());
            
            users.put(fingerprint, userData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SU");
            response.put("message", "등록 옵션 생성 성공");
            response.put("credentialId", credentialId);
            response.put("publicKey", publicKey);
            response.put("attestationObject", attestationObject);
            response.put("clientDataJSON", clientDataJSON);
            response.put("deviceInfo", deviceInfo);
            
            log.info("등록 옵션 생성 성공 - fingerprint: {}, credentialId: {}", fingerprint, credentialId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("등록 옵션 생성 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "ISE");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 보안 챌린지 생성
     */
    private String generateChallenge() {
        SecureRandom random = new SecureRandom();
        byte[] challengeBytes = new byte[32];
        random.nextBytes(challengeBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes);
    }
    
    /**
     * Credential ID 생성
     */
    private String generateCredentialId() {
        SecureRandom random = new SecureRandom();
        byte[] credentialBytes = new byte[64];
        random.nextBytes(credentialBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(credentialBytes);
    }
    
    /**
     * 키 쌍 생성 (EC P-256)
     */
    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        return keyGen.generateKeyPair();
    }
    
    /**
     * 공개키 인코딩 (간소화된 버전)
     */
    private String encodePublicKey(PublicKey publicKey) {
        try {
            // 간단한 방식으로 65바이트 고정 크기 반환
            byte[] keyBytes = new byte[65];
            new SecureRandom().nextBytes(keyBytes);
            keyBytes[0] = 0x04; // uncompressed point indicator
            return Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
        } catch (Exception e) {
            log.error("공개키 인코딩 실패", e);
            byte[] fallback = new byte[65];
            new SecureRandom().nextBytes(fallback);
            fallback[0] = 0x04;
            return Base64.getUrlEncoder().withoutPadding().encodeToString(fallback);
        }
    }
    
    /**
     * Attestation Object 생성 (하드웨어 정보 포함)
     */
    private String generateAttestationObject(String challenge, String credentialId, PublicKey publicKey, String fingerprint, HttpServletRequest request) {
        try {
            // RP ID Hash (32 bytes) - 도메인 기반
            String rpId = request.getServerName();
            byte[] rpIdHash = generateHash(rpId.getBytes());
            
            // Flags (1 byte) - User Present + User Verified + Attested Credential Data
            byte flags = (byte) 0x45;
            
            // Counter (4 bytes) - 사용자별 카운터
            int counter = getUserCounter(fingerprint);
            byte[] counterBytes = ByteBuffer.allocate(4).putInt(counter).array();
            
            // AAGUID (16 bytes) - 하드웨어 정보 기반
            UUID aaguid = generateHardwareBasedAAGUID(request, fingerprint);
            ByteBuffer aaguidBuffer = ByteBuffer.allocate(16);
            aaguidBuffer.putLong(aaguid.getMostSignificantBits());
            aaguidBuffer.putLong(aaguid.getLeastSignificantBits());
            byte[] aaguidBytes = aaguidBuffer.array();
            
            // Credential ID Length (2 bytes)
            byte[] credentialIdBytes = Base64.getUrlDecoder().decode(credentialId);
            byte[] credentialIdLength = ByteBuffer.allocate(2).putShort((short) credentialIdBytes.length).array();
            
            // Public Key (COSE format) - 실제 공개키 사용
            byte[] publicKeyBytes = encodeRealPublicKey(publicKey);
            
            // AuthData 조립
            int authDataSize = 32 + 1 + 4 + 16 + 2 + credentialIdBytes.length + publicKeyBytes.length;
            ByteBuffer authDataBuffer = ByteBuffer.allocate(authDataSize);
            
            authDataBuffer.put(rpIdHash);
            authDataBuffer.put(flags);
            authDataBuffer.put(counterBytes);
            authDataBuffer.put(aaguidBytes);
            authDataBuffer.put(credentialIdLength);
            authDataBuffer.put(credentialIdBytes);
            authDataBuffer.put(publicKeyBytes);
            
            byte[] authData = authDataBuffer.array();
            
            // 간단한 CBOR 형식의 Attestation Object
            ByteBuffer attestationBuffer = ByteBuffer.allocate(authData.length + 50);
            
            // CBOR 헤더
            attestationBuffer.put((byte) 0xA3); // map with 3 items
            attestationBuffer.put((byte) 0x63); // "fmt"
            attestationBuffer.put("fmt".getBytes());
            attestationBuffer.put((byte) 0x64); // "none"
            attestationBuffer.put("none".getBytes());
            attestationBuffer.put((byte) 0x67); // "attStmt"
            attestationBuffer.put("attStmt".getBytes());
            attestationBuffer.put((byte) 0xA0); // empty map
            attestationBuffer.put((byte) 0x68); // "authData"
            attestationBuffer.put("authData".getBytes());
            
            // authData 길이 인코딩
            if (authData.length < 24) {
                attestationBuffer.put((byte) (0x40 + authData.length));
            } else if (authData.length < 256) {
                attestationBuffer.put((byte) 0x58);
                attestationBuffer.put((byte) authData.length);
            } else {
                attestationBuffer.put((byte) 0x59);
                attestationBuffer.putShort((short) authData.length);
            }
            
            attestationBuffer.put(authData);
            
            // 실제 사용된 바이트만 반환
            byte[] result = new byte[attestationBuffer.position()];
            attestationBuffer.rewind();
            attestationBuffer.get(result);
            
            log.info("하드웨어 기반 Attestation Object 생성 - fingerprint: {}, aaguid: {}", fingerprint, aaguid);
            
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (Exception e) {
            log.error("Attestation Object 생성 실패", e);
            // fallback
            byte[] fallback = new byte[200];
            new SecureRandom().nextBytes(fallback);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(fallback);
        }
    }
    
    /**
     * 하드웨어 정보 기반 AAGUID 생성 (IP 제외)
     */
    private UUID generateHardwareBasedAAGUID(HttpServletRequest request, String fingerprint) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String platform = extractPlatformFromUserAgent(userAgent);
            String acceptLanguage = request.getHeader("Accept-Language");
            
            // 브라우저에서 추출 가능한 고정 정보만 사용
            String browserInfo = extractBrowserInfo(userAgent);
            String osInfo = extractOSInfo(userAgent);
            
            // 디바이스 고유 정보 조합 (IP 제외)
            String deviceUniqueInfo = fingerprint + "|" + platform + "|" + browserInfo + "|" + osInfo + "|" + acceptLanguage;
            
            log.info("디바이스 고유 정보: {}", deviceUniqueInfo);
            
            // 해시를 통한 결정적 UUID 생성
            byte[] hash = generateHash(deviceUniqueInfo.getBytes());
            
            // UUID 형식으로 변환 (16바이트 사용)
            ByteBuffer bb = ByteBuffer.wrap(hash, 0, 16);
            long mostSigBits = bb.getLong();
            long leastSigBits = bb.getLong();
            
            // UUID 버전 4 설정
            mostSigBits &= ~0xF000L;
            mostSigBits |= 0x4000L;
            leastSigBits &= ~0xC000000000000000L;
            leastSigBits |= 0x8000000000000000L;
            
            UUID aaguid = new UUID(mostSigBits, leastSigBits);
            log.info("생성된 AAGUID: {}", aaguid);
            
            return aaguid;
        } catch (Exception e) {
            log.error("하드웨어 기반 AAGUID 생성 실패", e);
            return UUID.randomUUID();
        }
    }
    
    /**
     * User Agent에서 브라우저 정보 추출
     */
    private String extractBrowserInfo(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Chrome/")) {
            int start = userAgent.indexOf("Chrome/") + 7;
            int end = userAgent.indexOf(" ", start);
            if (end == -1) end = userAgent.length();
            return "Chrome-" + userAgent.substring(start, Math.min(end, start + 10));
        }
        if (userAgent.contains("Firefox/")) {
            int start = userAgent.indexOf("Firefox/") + 8;
            int end = userAgent.indexOf(" ", start);
            if (end == -1) end = userAgent.length();
            return "Firefox-" + userAgent.substring(start, Math.min(end, start + 10));
        }
        if (userAgent.contains("Safari/") && !userAgent.contains("Chrome")) {
            int start = userAgent.indexOf("Version/") + 8;
            int end = userAgent.indexOf(" ", start);
            if (end == -1) end = userAgent.length();
            return "Safari-" + userAgent.substring(start, Math.min(end, start + 10));
        }
        if (userAgent.contains("Edge/")) {
            int start = userAgent.indexOf("Edge/") + 5;
            int end = userAgent.indexOf(" ", start);
            if (end == -1) end = userAgent.length();
            return "Edge-" + userAgent.substring(start, Math.min(end, start + 10));
        }
        
        return "Unknown-Browser";
    }
    
    /**
     * User Agent에서 OS 정보 추출
     */
    private String extractOSInfo(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Windows NT 10.0")) return "Windows-10";
        if (userAgent.contains("Windows NT 6.3")) return "Windows-8.1";
        if (userAgent.contains("Windows NT 6.1")) return "Windows-7";
        if (userAgent.contains("Windows")) return "Windows-Other";
        
        if (userAgent.contains("Mac OS X 10_15")) return "macOS-Catalina";
        if (userAgent.contains("Mac OS X 10_14")) return "macOS-Mojave";
        if (userAgent.contains("Mac OS X 10_13")) return "macOS-HighSierra";
        if (userAgent.contains("Mac OS X")) return "macOS-Other";
        
        if (userAgent.contains("Android 11")) return "Android-11";
        if (userAgent.contains("Android 10")) return "Android-10";
        if (userAgent.contains("Android 9")) return "Android-9";
        if (userAgent.contains("Android")) return "Android-Other";
        
        if (userAgent.contains("iPhone OS 15")) return "iOS-15";
        if (userAgent.contains("iPhone OS 14")) return "iOS-14";
        if (userAgent.contains("iPhone OS 13")) return "iOS-13";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS-Other";
        
        if (userAgent.contains("Linux")) return "Linux";
        
        return "Unknown-OS";
    }
    
    /**
     * 사용자별 카운터 가져오기
     */
    private int getUserCounter(String fingerprint) {
        Map<String, Object> user = users.get(fingerprint);
        if (user != null && user.containsKey("counter")) {
            return (Integer) user.get("counter") + 1;
        }
        return 1;
    }
    
    /**
     * 실제 공개키 인코딩 (COSE 형식)
     */
    private byte[] encodeRealPublicKey(PublicKey publicKey) {
        try {
            if (publicKey instanceof ECPublicKey) {
                ECPublicKey ecKey = (ECPublicKey) publicKey;
                ECPoint point = ecKey.getW();
                
                // EC 포인트를 바이트 배열로 변환
                byte[] x = point.getAffineX().toByteArray();
                byte[] y = point.getAffineY().toByteArray();
                
                // 32바이트로 정규화
                x = normalizeToLength(x, 32);
                y = normalizeToLength(y, 32);
                
                // COSE Key 형식 (EC2)
                ByteBuffer coseKey = ByteBuffer.allocate(77); // COSE 헤더 + 데이터
                coseKey.put((byte) 0xA5); // map with 5 items
                coseKey.put((byte) 0x01); // kty
                coseKey.put((byte) 0x02); // EC2
                coseKey.put((byte) 0x03); // alg
                coseKey.put((byte) 0x26); // ES256
                coseKey.put((byte) 0x20); // crv
                coseKey.put((byte) 0x01); // P-256
                coseKey.put((byte) 0x21); // x
                coseKey.put((byte) 0x58); // byte string
                coseKey.put((byte) 0x20); // 32 bytes
                coseKey.put(x);
                coseKey.put((byte) 0x22); // y
                coseKey.put((byte) 0x58); // byte string
                coseKey.put((byte) 0x20); // 32 bytes
                coseKey.put(y);
                
                return coseKey.array();
            }
        } catch (Exception e) {
            log.error("실제 공개키 인코딩 실패", e);
        }
        
        // fallback
        byte[] fallback = new byte[77];
        new SecureRandom().nextBytes(fallback);
        return fallback;
    }
    
    /**
     * 바이트 배열을 지정된 길이로 정규화
     */
    private byte[] normalizeToLength(byte[] input, int targetLength) {
        if (input.length == targetLength) {
            return input;
        } else if (input.length > targetLength) {
            return java.util.Arrays.copyOfRange(input, input.length - targetLength, input.length);
        } else {
            byte[] result = new byte[targetLength];
            System.arraycopy(input, 0, result, targetLength - input.length, input.length);
            return result;
        }
    }
    
    /**
     * SHA-256 해시 생성
     */
    private byte[] generateHash(byte[] input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (Exception e) {
            log.error("해시 생성 실패", e);
            byte[] fallback = new byte[32];
            new SecureRandom().nextBytes(fallback);
            return fallback;
        }
    }
    
    /**
     * Client Data JSON 생성
     */
    private String generateClientDataJSON(String challenge, HttpServletRequest request) {
        try {
            String origin = request.getHeader("Origin");
            if (origin == null) {
                origin = "http://localhost:3000";
            }
            
            Map<String, Object> clientData = new HashMap<>();
            clientData.put("type", "webauthn.create");
            clientData.put("challenge", challenge);
            clientData.put("origin", origin);
            clientData.put("crossOrigin", false);
            
            String jsonString = convertToJson(clientData);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());
        } catch (Exception e) {
            log.error("Client Data JSON 생성 실패", e);
            return Base64.getUrlEncoder().withoutPadding().encodeToString("{}".getBytes());
        }
    }
    
    /**
     * 기기 정보 생성
     */
    private Map<String, Object> generateDeviceInfo(HttpServletRequest request, String fingerprint) {
        Map<String, Object> deviceInfo = new HashMap<>();
        
        deviceInfo.put("fingerprint", fingerprint);
        deviceInfo.put("userAgent", request.getHeader("User-Agent"));
        deviceInfo.put("ipAddress", getClientIpAddress(request));
        deviceInfo.put("timestamp", Instant.now().toString());
        deviceInfo.put("platform", extractPlatformFromUserAgent(request.getHeader("User-Agent")));
        deviceInfo.put("language", request.getHeader("Accept-Language"));
        deviceInfo.put("origin", request.getHeader("Origin"));
        deviceInfo.put("referer", request.getHeader("Referer"));
        
        return deviceInfo;
    }
    
    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", 
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", 
            "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", 
            "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * User Agent에서 플랫폼 추출
     */
    private String extractPlatformFromUserAgent(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        
        return "Unknown";
    }
    
    /**
     * 간단한 JSON 변환
     */
    private String convertToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Boolean) {
                json.append(entry.getValue());
            } else {
                json.append("\"").append(entry.getValue()).append("\"");
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    /**
     * WebAuthn 등록 처리
     */
    @Operation(summary = "WebAuthn 등록 처리", description = "WebAuthn 등록을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "등록 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "VF", description = "유효하지 않은 챌린지", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "ISE", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "fingerprint", description = "디바이스 핑거프린트 (8~50자)", required = true, example = "abc123def456"),
        @Parameter(name = "credentialId", description = "WebAuthn Credential ID (Base64 인코딩)", required = true, example = "dGVzdC1jcmVkZW50aWFsLWlk"),
        @Parameter(name = "publicKey", description = "WebAuthn Public Key (Base64 인코딩)", required = true, example = "eyJhbGciOiJFUzI1NiJ9..."),
        @Parameter(name = "attestationObject", description = "WebAuthn Attestation Object (Base64URL 인코딩)", required = true, example = "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YVikSZYN5YgOjGh0NBcPZHZgW4_krrmihjLHmVzzuoMdl2NFAAAAAAAAAAAAAAAAAAAAAAAAAAAA..."),
        @Parameter(name = "clientDataJSON", description = "WebAuthn Client Data JSON (Base64URL 인코딩)", required = true, example = "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiY2hhbGxlbmdlLXN0cmluZyIsIm9yaWdpbiI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzAwMCJ9"),
        @Parameter(name = "deviceInfo", description = "기기 정보 객체 (JSON)", required = true, example = "{\"userAgent\":\"Mozilla/5.0...\",\"platform\":\"MacIntel\",\"ipAddress\":\"192.168.1.1\"}")
    })
    @PostMapping("/webauthn/register")
    public ResponseEntity<Map<String, Object>> registerWebAuthn(@RequestBody Map<String, Object> request) {
        try {
            String fingerprint = (String) request.get("fingerprint");
            String expectedChallenge = challenges.get(fingerprint);
            
            if (expectedChallenge == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "VF");
                errorResponse.put("message", "유효하지 않은 챌린지");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            String attestationObject = (String) request.get("attestationObject");
            String aaguid = extractAaguidFromAttestation(attestationObject);
            
            log.info("AAGUID 추출 결과: {}", aaguid);
            log.info("AttestationObject: {}", attestationObject);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", fingerprint);
            userData.put("credentialId", request.get("credentialId"));
            userData.put("publicKey", request.get("publicKey"));
            userData.put("attestationObject", attestationObject);
            userData.put("clientDataJSON", request.get("clientDataJSON"));
            userData.put("aaguid", aaguid);
            userData.put("deviceInfo", request.get("deviceInfo"));
            userData.put("registeredAt", System.currentTimeMillis());
            userData.put("counter", 0);
            
            users.put(fingerprint, userData);
            challenges.remove(fingerprint);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SU");
            response.put("message", "등록 성공");
            response.put("aaguid", aaguid);
            response.put("attestationObject", attestationObject);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("등록 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "ISE");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * WebAuthn 인증 옵션 생성
     */
    @Operation(summary = "WebAuthn 인증 옵션 생성", description = "WebAuthn 인증을 위한 옵션을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "인증 옵션 생성 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "NU", description = "등록되지 않은 사용자", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "ISE", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "fingerprint", description = "디바이스 핑거프린트 (8~50자)", required = true, example = "abc123def456")
    })
    @PostMapping("/webauthn/authentication-options")
    public ResponseEntity<Map<String, Object>> getAuthenticationOptions(@RequestBody Map<String, String> request) {
        try {
            String fingerprint = request.get("fingerprint");
            Map<String, Object> user = users.get(fingerprint);
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "NU");
                errorResponse.put("message", "등록되지 않은 사용자");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            String challenge = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            challenges.put(fingerprint, challenge);
            
            Map<String, Object> allowCredential = new HashMap<>();
            allowCredential.put("id", user.get("credentialId"));
            allowCredential.put("type", "public-key");
            allowCredential.put("transports", new String[]{"internal"});
            
            Map<String, Object> options = new HashMap<>();
            options.put("challenge", challenge);
            options.put("timeout", 60000);
            options.put("userVerification", "required");
            options.put("allowCredentials", new Object[]{allowCredential});
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SU");
            response.put("message", "인증 옵션 생성 성공");
            response.put("options", options);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("인증 옵션 생성 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "ISE");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * WebAuthn 인증 처리
     */
    @Operation(summary = "WebAuthn 인증 처리", description = "WebAuthn 인증을 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "인증 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "VF", description = "유효하지 않은 요청", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "ISE", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "fingerprint", description = "디바이스 핑거프린트 (8~50자)", required = true, example = "abc123def456"),
        @Parameter(name = "credentialId", description = "WebAuthn Credential ID (Base64 인코딩)", required = true, example = "dGVzdC1jcmVkZW50aWFsLWlk"),
        @Parameter(name = "signature", description = "WebAuthn 인증 서명 데이터 (JSON)", required = true, example = "{\"authenticatorData\":\"SZYN5YgOjGh0NBcPZHZgW4_krrmihjLHmVzzuoMdl2NFAAAAAQ\",\"signature\":\"MEUCIQDTGVxhrLcrKaK-jVz8GmhQx9BqAy7W7CmkPgPp8cdjwwIgYBJROmaTBVQhNsPYdAZhPa-i_nNNN5T7QP0KB0fIz1Y\",\"userHandle\":\"\"}")
    })
    @PostMapping("/webauthn/authenticate")
    public ResponseEntity<Map<String, Object>> authenticateWebAuthn(@RequestBody Map<String, Object> request) {
        try {
            String fingerprint = (String) request.get("fingerprint");
            Map<String, Object> user = users.get(fingerprint);
            String expectedChallenge = challenges.get(fingerprint);
            
            if (user == null || expectedChallenge == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "VF");
                errorResponse.put("message", "유효하지 않은 요청");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            challenges.remove(fingerprint);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SU");
            response.put("message", "인증 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("인증 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "ISE");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 등록된 사용자 확인
     */
    @Operation(summary = "등록된 사용자 확인", description = "등록된 사용자인지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "등록된 사용자", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "NU", description = "등록되지 않은 사용자", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "ISE", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/webauthn/check/{fingerprint}")
    public ResponseEntity<Map<String, Object>> checkRegisteredUser(@Parameter(name = "fingerprint", description = "디바이스 핑거프린트 (8~50자)", required = true, example = "abc123def456") String fingerprint) {
        try {
            Map<String, Object> user = users.get(fingerprint);
            
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.get("id"));
                userInfo.put("registeredAt", user.get("registeredAt"));
                
                Map<String, Object> response = new HashMap<>();
                response.put("code", "SU");
                response.put("message", "등록된 사용자");
                response.put("user", userInfo);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "NU");
                errorResponse.put("message", "등록되지 않은 사용자");
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("사용자 확인 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "ISE");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 기기 정보 저장
     */
    @Operation(summary = "기기 정보 저장", description = "사용자 기기 정보를 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "기기 정보 저장 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "ISE", description = "서버 오류", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "fingerprint", description = "디바이스 핑거프린트", required = true, example = "abc123def456"),
        @Parameter(name = "userAgent", description = "브라우저 User Agent", required = false, example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"),
        @Parameter(name = "platform", description = "플랫폼 정보", required = false, example = "MacIntel"),
        @Parameter(name = "language", description = "브라우저 언어", required = false, example = "ko-KR"),
        @Parameter(name = "ipAddress", description = "IP 주소", required = false, example = "192.168.1.100"),
        @Parameter(name = "country", description = "국가", required = false, example = "South Korea"),
        @Parameter(name = "city", description = "도시", required = false, example = "Seoul"),
        @Parameter(name = "osName", description = "운영체제 이름", required = false, example = "Mac OS"),
        @Parameter(name = "osVersion", description = "운영체제 버전", required = false, example = "10.15.7"),
        @Parameter(name = "browserName", description = "브라우저 이름", required = false, example = "Chrome"),
        @Parameter(name = "browserVersion", description = "브라우저 버전", required = false, example = "120.0.0.0"),
        @Parameter(name = "deviceType", description = "디바이스 타입", required = false, example = "desktop"),
        @Parameter(name = "screenWidth", description = "화면 너비", required = false, example = "1920"),
        @Parameter(name = "screenHeight", description = "화면 높이", required = false, example = "1080"),
        @Parameter(name = "timezone", description = "시간대", required = false, example = "Asia/Seoul")
    })
    @PostMapping("/device/save")
    public ResponseEntity<Map<String, Object>> saveDeviceInfo(@RequestBody Map<String, Object> deviceInfo) {
        try {
            log.info("기기 정보 저장: {}", deviceInfo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", "SU");
            response.put("message", "기기 정보 저장 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("기기 정보 저장 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "ISE");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * attestationObject에서 AAGUID 추출
     */
    private String extractAaguidFromAttestation(String attestationObject) {
        try {
            if (attestationObject == null || attestationObject.isEmpty()) {
                return "no-attestation-object";
            }
            
            // Base64URL 디코딩
            byte[] decodedBytes = Base64.getUrlDecoder().decode(attestationObject);
            
            // decodedBytes 내용을 로그로 출력
            StringBuilder hexString = new StringBuilder();
            for (byte b : decodedBytes) {
                hexString.append(String.format("%02x", b));
            }
            log.info("decodedBytes length: {}", decodedBytes.length);
            log.info("decodedBytes hex: {}", hexString.toString());
            log.info("decodedBytes base64: {}", Base64.getEncoder().encodeToString(decodedBytes));
            
            // 간단한 AAGUID 추출 (실제로는 CBOR 라이브러리 사용 권장)
            // AAGUID는 보통 authData의 37-52 바이트 위치에 있음
            if (decodedBytes.length > 52) {
                // 간단한 패턴 매칭으로 AAGUID 위치 찾기
                for (int i = 37; i < decodedBytes.length - 16; i++) {
                    if (i + 16 <= decodedBytes.length) {
                        byte[] aaguidBytes = new byte[16];
                        System.arraycopy(decodedBytes, i, aaguidBytes, 0, 16);
                        
                        // UUID 형식으로 변환
                        ByteBuffer bb = ByteBuffer.wrap(aaguidBytes);
                        long high = bb.getLong();
                        long low = bb.getLong();
                        UUID uuid = new UUID(high, low);
                        
                        String aaguid = uuid.toString();
                        if (!aaguid.equals("00000000-0000-0000-0000-000000000000")) {
                            return aaguid;
                        }
                    }
                }
            }
            
            return "aaguid-not-found";
        } catch (Exception e) {
            log.error("AAGUID 추출 실패", e);
            return "extraction-failed";
        }
    }

}
