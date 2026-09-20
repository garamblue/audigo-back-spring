package com.edid.edid_back.controller.member;

import com.edid.edid_back.service.auth.MemberAuthService;
import com.edid.edid_back.service.member.MemberProfileService;
import com.edid.edid_back.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

/**
 * 회원 프로필 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberAuthService memberAuthService;
    private final MemberProfileService memberProfileService;
    private final AesUtil aesUtil;

    /**
     * 회원 프로필 조회
     * GET /api/member/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());

            Map<String, Object> profileData = memberAuthService.getMemberProfile(mIdx);

            // 응답 암호화
            String encryptedResponse = aesUtil.encryptMember(profileData);

            return ResponseEntity.ok(Map.of("data", encryptedResponse));

        } catch (Exception e) {
            log.error("프로필 조회 오류", e);
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", "프로필 조회 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }

    /**
     * 닉네임 중복 확인
     * POST /api/member/nickname/check
     *
     * Request: {"nickname": "닉네임"}
     */
    @PostMapping("/nickname/check")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestBody Map<String, String> request) {
        try {
            String encryptedData = request.get("data");

            if (encryptedData == null || encryptedData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("msg", "Missing encrypted data"));
            }

            // 복호화
            Map<String, Object> data = aesUtil.decryptMember(encryptedData, Map.class);
            String nickname = (String) data.get("nickname");

            if (nickname == null || nickname.isEmpty()) {
                Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "닉네임을 입력해주세요."
                );
                String encryptedError = aesUtil.encryptMember(errorResponse);
                return ResponseEntity.badRequest().body(Map.of("data", encryptedError));
            }

            boolean isDuplicate = memberProfileService.checkNicknameDuplicate(nickname);

            if (isDuplicate) {
                // 중복된 닉네임
                Map<String, Object> response = Map.of(
                    "code", "0",
                    "msg", "이미 사용 중인 닉네임입니다."
                );
                String encryptedResponse = aesUtil.encryptMember(response);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("data", encryptedResponse));
            } else {
                // 사용 가능한 닉네임
                Map<String, Object> response = Map.of(
                    "code", "1",
                    "msg", "사용 가능한 닉네임입니다."
                );
                String encryptedResponse = aesUtil.encryptMember(response);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("data", encryptedResponse));
            }

        } catch (Exception e) {
            log.error("닉네임 중복 확인 오류", e);
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", "닉네임 확인 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }

    /**
     * 닉네임 변경
     * PUT /api/member/nickname/update
     *
     * Request: {"nickname": "새닉네임"}
     */
    @PutMapping("/nickname/update")
    public ResponseEntity<Map<String, Object>> updateNickname(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String encryptedData = request.get("data");

            if (encryptedData == null || encryptedData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("msg", "Missing encrypted data"));
            }

            // 복호화
            Map<String, Object> data = aesUtil.decryptMember(encryptedData, Map.class);
            String newNickname = (String) data.get("nickname");

            if (newNickname == null || newNickname.isEmpty()) {
                Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "닉네임을 입력해주세요."
                );
                String encryptedError = aesUtil.encryptMember(errorResponse);
                return ResponseEntity.badRequest().body(Map.of("data", encryptedError));
            }

            // 닉네임 변경
            memberProfileService.updateNickname(mIdx, newNickname);

            Map<String, Object> response = Map.of(
                "code", "1",
                "msg", "닉네임이 변경되었습니다."
            );
            String encryptedResponse = aesUtil.encryptMember(response);
            return ResponseEntity.ok(Map.of("data", encryptedResponse));

        } catch (IllegalArgumentException e) {
            log.error("닉네임 변경 validation 오류: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", e.getMessage()
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.badRequest().body(Map.of("data", encryptedError));

        } catch (Exception e) {
            log.error("닉네임 변경 오류", e);
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", "닉네임 변경 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }

    /**
     * 지역 상태 변경
     * PUT /api/member/state/update
     *
     * Request: {"state": "US"}
     */
    @PutMapping("/state/update")
    public ResponseEntity<Map<String, Object>> updateState(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String encryptedData = request.get("data");

            if (encryptedData == null || encryptedData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("msg", "Missing encrypted data"));
            }

            // 복호화
            Map<String, Object> data = aesUtil.decryptMember(encryptedData, Map.class);
            String newState = (String) data.get("state");

            if (newState == null || newState.isEmpty()) {
                Map<String, Object> errorResponse = Map.of(
                    "code", "0",
                    "msg", "지역 상태를 입력해주세요."
                );
                String encryptedError = aesUtil.encryptMember(errorResponse);
                return ResponseEntity.badRequest().body(Map.of("data", encryptedError));
            }

            // 상태 변경
            memberProfileService.updateState(mIdx, newState);

            Map<String, Object> response = Map.of(
                "code", "1",
                "msg", "지역 상태가 변경되었습니다."
            );
            String encryptedResponse = aesUtil.encryptMember(response);
            return ResponseEntity.ok(Map.of("data", encryptedResponse));

        } catch (Exception e) {
            log.error("지역 상태 변경 오류", e);
            Map<String, Object> errorResponse = Map.of(
                "code", "0",
                "msg", "지역 상태 변경 중 오류가 발생했습니다."
            );
            String encryptedError = aesUtil.encryptMember(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("data", encryptedError));
        }
    }
}
