package com.audigo.audigo_back.service.auth;

import com.audigo.audigo_back.entity.auth.MobileVerifyHisEntity;
import com.audigo.audigo_back.repository.auth.MobileVerifyHisRepository;
import com.audigo.audigo_back.util.AesUtil;
import com.audigo.audigo_back.util.CodeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * SMS 인증 서비스
 * NHN Cloud SMS API 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsAuthService {

    private final MobileVerifyHisRepository mobileVerifyHisRepository;
    private final AesUtil aesUtil;
    private final CodeUtil codeUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${nhn.sms.app-key}")
    private String nhnAppKey;

    @Value("${nhn.sms.secret-key}")
    private String nhnSecretKey;

    @Value("${nhn.sms.send-no}")
    private String nhnSendNo;

    /**
     * SMS 인증 코드 요청
     * - 하루 3회 제한
     * - 3분 유효
     */
    @Transactional
    public void requestAuthCode(String encryptedData) {
        try {
            // 1. 복호화
            Map<String, Object> decrypted = aesUtil.decryptMember(encryptedData, Map.class);
            String mobileNum = (String) decrypted.get("mobile_num");

            if (mobileNum == null || mobileNum.isEmpty()) {
                throw new IllegalArgumentException("휴대폰 번호가 필요합니다.");
            }

            // 2. 오늘 요청 횟수 확인 (3회 제한)
            Long todayCount = mobileVerifyHisRepository.countTodayRequests(mobileNum);
            if (todayCount >= 3) {
                throw new IllegalStateException("오늘 인증 요청 횟수를 초과했습니다. 내일 다시 시도해주세요.");
            }

            // 3. 인증 코드 생성
            String authCode = codeUtil.generateAuthCode();

            // 4. SMS 발송
            sendSmsAuthCode(mobileNum, authCode);

            // 5. 인증 이력 저장
            MobileVerifyHisEntity entity = new MobileVerifyHisEntity();
            entity.setMobileNum(mobileNum);
            entity.setAuthCd(authCode);
            entity.setExpDt(LocalDateTime.now().plusMinutes(3)); // 3분 후 만료
            entity.setCdt(LocalDateTime.now());
            entity.setExpYn("N");

            mobileVerifyHisRepository.save(entity);

            log.info("SMS 인증 코드 발송 완료: {}", mobileNum);

        } catch (Exception e) {
            log.error("SMS 인증 코드 요청 실패", e);
            throw new RuntimeException("SMS 인증 코드 요청에 실패했습니다.", e);
        }
    }

    /**
     * SMS 인증 코드 확인
     */
    @Transactional
    public void verifyAuthCode(String encryptedData) {
        try {
            // 1. 복호화
            Map<String, Object> decrypted = aesUtil.decryptMember(encryptedData, Map.class);
            String mobileNum = (String) decrypted.get("mobile_num");
            String code = (String) decrypted.get("code");

            if (mobileNum == null || code == null) {
                throw new IllegalArgumentException("휴대폰 번호와 인증 코드가 필요합니다.");
            }

            // 2. 유효한 인증 코드 확인
            MobileVerifyHisEntity entity = mobileVerifyHisRepository
                    .findValidAuthCode(mobileNum, code)
                    .orElseThrow(() -> new IllegalStateException("유효하지 않은 인증 코드입니다."));

            // 3. 인증 완료 처리 (만료 처리)
            entity.setExpDt(LocalDateTime.now());
            entity.setExpYn("Y");
            mobileVerifyHisRepository.save(entity);

            log.info("SMS 인증 확인 완료: {}", mobileNum);

        } catch (Exception e) {
            log.error("SMS 인증 확인 실패", e);
            throw new RuntimeException("SMS 인증 확인에 실패했습니다.", e);
        }
    }

    /**
     * NHN Cloud SMS API를 통한 SMS 발송
     */
    private void sendSmsAuthCode(String mobileNum, String authCode) {
        try {
            // 1. 전화번호 파싱 (국제 번호 형식 처리)
            String recipientNo = parsePhoneNumber(mobileNum);
            String internationalRecipientNo = recipientNo.startsWith("+") ? recipientNo : "+" + recipientNo;

            // 2. SMS 본문 생성
            String body = String.format("[Audigo] 인증번호 [%s]", authCode);

            // 3. 인증 키워드 확인
            String[] authKeywords = {"auth", "password", "verify", "인증", "비밀번호", "認証", "にんしょう"};
            boolean hasKeyword = Arrays.stream(authKeywords)
                    .anyMatch(k -> body.toLowerCase().contains(k.toLowerCase()));

            if (!hasKeyword) {
                throw new IllegalStateException("SMS 본문에 인증 키워드가 포함되어 있지 않습니다.");
            }

            // 4. 요청 페이로드 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("body", body);
            payload.put("sendNo", nhnSendNo);
            payload.put("senderGroupingKey", "defaultGroup");

            List<Map<String, Object>> recipientList = new ArrayList<>();
            Map<String, Object> recipient = new HashMap<>();
            recipient.put("recipientNo", recipientNo);
            recipient.put("internationalRecipientNo", internationalRecipientNo);
            recipient.put("recipientGroupingKey", "defaultRecipientGroup");
            recipient.put("templateParameter", new HashMap<>());
            recipientList.add(recipient);

            payload.put("recipientList", recipientList);
            payload.put("userId", "system");
            payload.put("statsId", "statsId");

            // 5. HTTP 요청
            String url = String.format("https://api-sms.cloud.toast.com/sms/v3.0/appKeys/%s/sender/auth/sms", nhnAppKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Secret-Key", nhnSecretKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            String response = restTemplate.postForObject(url, request, String.class);

            // 6. 응답 확인
            JsonNode jsonNode = objectMapper.readTree(response);
            boolean isSuccessful = jsonNode.path("header").path("isSuccessful").asBoolean();

            if (!isSuccessful) {
                String resultCode = jsonNode.path("header").path("resultCode").asText();
                String resultMessage = jsonNode.path("header").path("resultMessage").asText();
                throw new RuntimeException(String.format("SMS 발송 실패: %s %s", resultCode, resultMessage));
            }

            log.info("SMS 발송 성공: {}", mobileNum);

        } catch (Exception e) {
            log.error("SMS 발송 실패", e);
            throw new RuntimeException("SMS 발송에 실패했습니다.", e);
        }
    }

    /**
     * 전화번호 파싱 (국제 번호 형식으로 변환)
     */
    private String parsePhoneNumber(String phone) {
        // +로 시작하지 않으면 + 추가
        return phone.startsWith("+") ? phone : "+" + phone;
    }
}
