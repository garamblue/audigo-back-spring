package com.audigo.audigo_back.controller.auth;

import com.audigo.audigo_back.service.auth.SmsAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SMS 인증 컨트롤러
 */
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SMS 인증", description = "SMS 인증 코드 요청 및 확인 API")
public class SmsAuthController {

    private final SmsAuthService smsAuthService;

    /**
     * SMS 인증 코드 요청
     * @param data 암호화된 요청 데이터 {mobile_num: string}
     */
    @GetMapping("/mbr/authcode/req")
    @Operation(summary = "SMS 인증 코드 요청", description = "휴대폰 번호로 SMS 인증 코드 발송 (하루 3회 제한, 3분 유효)")
    public ResponseEntity<Map<String, Object>> requestAuthCode(@RequestParam String data) {
        try {
            smsAuthService.requestAuthCode(data);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "SMS 인증 코드가 발송되었습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.warn("SMS 인증 코드 요청 제한: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("SMS 인증 코드 요청 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "SMS 인증 코드 요청에 실패했습니다.");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * SMS 인증 코드 확인
     * @param data 암호화된 요청 데이터 {mobile_num: string, code: string}
     */
    @GetMapping("/mbr/authcode/authentication")
    @Operation(summary = "SMS 인증 코드 확인", description = "발송된 SMS 인증 코드 확인")
    public ResponseEntity<Map<String, Object>> verifyAuthCode(@RequestParam String data) {
        try {
            smsAuthService.verifyAuthCode(data);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "인증이 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.warn("SMS 인증 확인 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("SMS 인증 확인 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "SMS 인증 확인에 실패했습니다.");

            return ResponseEntity.status(500).body(response);
        }
    }
}
