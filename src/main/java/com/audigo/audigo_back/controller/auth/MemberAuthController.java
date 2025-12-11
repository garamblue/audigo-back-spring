package com.audigo.audigo_back.controller.auth;

import com.audigo.audigo_back.service.auth.MemberAuthService;
import com.audigo.audigo_back.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 회원 인증 컨트롤러
 * - 회원가입, 로그인 처리
 */
@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberAuthService;
    private final AesUtil aesUtil;

    /**
     * 회원가입
     * POST /api/member/signup
     *
     * Request Body (encrypted):
     * {
     *   "data": "encrypted_json_string"
     * }
     *
     * Decrypted Request:
     * {
     *   "email": "user@example.com",
     *   "nickname": "닉네임",
     *   "birthDt": "19900101",
     *   "gender": "M",
     *   "state": "US",
     *   "mobileNum": "01012345678",
     *   "invitCd": "ABCD1234EFGH" (optional),
     *   "snsType": "KAKAO",
     *   "extKey": "kakao_user_id",
     *   "regionCd": "KR",
     *   "terms": {
     *     "terms": "Y",
     *     "privacy": "Y",
     *     "push": "N",
     *     "night": "N",
     *     "email": "N",
     *     "web3": "Y"
     *   }
     * }
     *
     * Response (encrypted):
     * {
     *   "data": "encrypted_json_string"
     * }
     *
     * Decrypted Response (Success):
     * {
     *   "code": "1",
     *   "msg": "success",
     *   "data": {
     *     "mIdx": 123456,
     *     "accessToken": "jwt_access_token",
     *     "refreshToken": "jwt_refresh_token",
     *     "balance": 0,
     *     "tokenAmt": 0,
     *     "bnbAmt": 0
     *   }
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody Map<String, String> request) {
        try {
            String encryptedData = request.get("data");

            if (encryptedData == null || encryptedData.isEmpty()) {
                Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "Missing encrypted data"
                );
                String encryptedError = aesUtil.encryptMember(errorResponse);
                return ResponseEntity.badRequest().body(Map.of("data", encryptedError));
            }

            // 회원가입 처리
            Map<String, Object> response = memberAuthService.signUp(encryptedData);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("SignUp validation error: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", e.getMessage()
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.badRequest().body(Map.of("data", encryptedError));

        } catch (Exception e) {
            log.error("SignUp error", e);
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", "회원가입 처리 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }

    /**
     * 로그인
     * POST /api/member/signin
     *
     * Request Body (encrypted):
     * {
     *   "data": "encrypted_json_string"
     * }
     *
     * Decrypted Request:
     * {
     *   "snsType": "KAKAO",
     *   "extKey": "kakao_user_id",
     *   "email": "user@example.com" (optional),
     *   "mobileNum": "01012345678" (optional),
     *   "birthDt": "19900101" (optional)
     * }
     *
     * Response (encrypted):
     * {
     *   "data": "encrypted_json_string"
     * }
     *
     * Decrypted Response (Login Success - code=1):
     * {
     *   "code": "1",
     *   "msg": "success",
     *   "data": {
     *     "mIdx": 123456,
     *     "email": "user@example.com",
     *     "nickname": "닉네임",
     *     "stts": "Y",
     *     "state": "US",
     *     "regionCd": "KR",
     *     "snsList": ["KAKAO", "NAVER"],
     *     "accessToken": "jwt_access_token",
     *     "refreshToken": "jwt_refresh_token",
     *     "balance": 1000.50,
     *     "tokenAmt": 500.25,
     *     "bnbAmt": 0.05
     *   }
     * }
     *
     * Decrypted Response (Duplicate Account - code=2):
     * {
     *   "code": "2",
     *   "msg": "duplicate",
     *   "data": {
     *     "mIdx": 123456,
     *     "snsList": ["KAKAO", "NAVER"],
     *     "requestedSnsType": "GOOGLE"
     *   }
     * }
     *
     * Decrypted Response (New Signup Required - code=3):
     * {
     *   "code": "3",
     *   "msg": "signup"
     * }
     */
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> request) {
        try {
            String encryptedData = request.get("data");

            if (encryptedData == null || encryptedData.isEmpty()) {
                Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "Missing encrypted data"
                );
                String encryptedError = aesUtil.encryptMember(errorResponse);
                return ResponseEntity.badRequest().body(Map.of("data", encryptedError));
            }

            // 로그인 처리
            Map<String, Object> response = memberAuthService.signIn(encryptedData);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("SignIn validation error: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", e.getMessage()
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.badRequest().body(Map.of("data", encryptedError));

        } catch (Exception e) {
            log.error("SignIn error", e);
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", "로그인 처리 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }
}
