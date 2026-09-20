package com.audigo.audigo_back.service.market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GiftiShow API Integration Service
 * API Documentation: https://bizapi.giftishow.com
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiftishowApiService {

    private static final String API_BASE_URL = "https://bizapi.giftishow.com/bizApi";

    @Value("${giftishow.api.key:}")
    private String apiKey;

    @Value("${giftishow.api.token:}")
    private String apiToken;

    @Value("${giftishow.api.user-id:}")
    private String userId;

    @Value("${giftishow.api.sender:}")
    private String senderPhone;

    @Value("${giftishow.api.card-id:}")
    private String cardId;

    @Value("${giftishow.api.banner-id:}")
    private String bannerId;

    private final RestTemplate restTemplate;

    /**
     * Sync brands from GiftiShow (API Code: 0102)
     */
    public Map<String, Object> syncBrands() {
        log.info("Syncing brands from GiftiShow API");

        Map<String, String> request = new HashMap<>();
        request.put("api_code", "0102");
        request.put("custom_auth_code", apiKey);
        request.put("custom_auth_token", apiToken);
        request.put("dev_yn", "N");

        return callApi("/brands", request);
    }

    /**
     * Sync goods/products from GiftiShow (API Code: 0101)
     */
    public Map<String, Object> syncGoods() {
        log.info("Syncing goods from GiftiShow API");

        Map<String, String> request = new HashMap<>();
        request.put("api_code", "0101");
        request.put("custom_auth_code", apiKey);
        request.put("custom_auth_token", apiToken);
        request.put("dev_yn", "N");
        request.put("start", "1");
        request.put("size", "2200");

        return callApi("/goods", request);
    }

    /**
     * Send coupon/gift (API Code: 0204)
     */
    public Map<String, Object> sendCoupon(String goodsCode, String phoneNo, String trId) {
        log.info("Sending coupon: goodsCode={}, phoneNo={}, trId={}", goodsCode, phoneNo, trId);

        Map<String, String> request = new HashMap<>();
        request.put("api_code", "0204");
        request.put("custom_auth_code", apiKey);
        request.put("custom_auth_token", apiToken);
        request.put("dev_yn", "N");
        request.put("goods_code", goodsCode);
        request.put("mms_msg", "AUDIGO 쿠폰 발송");
        request.put("mms_title", "AUDIGO 쿠폰");
        request.put("callback_no", senderPhone);
        request.put("phone_no", phoneNo);
        request.put("tr_id", trId);
        request.put("template_id", cardId);
        request.put("banner_id", bannerId);
        request.put("user_id", userId);
        request.put("gubun", "I");

        return callApi("/send", request);
    }

    /**
     * Cancel exchange (API Code: 0202)
     */
    public Map<String, Object> cancelExchange(String trId) {
        log.info("Cancelling exchange: trId={}", trId);

        Map<String, String> request = new HashMap<>();
        request.put("api_code", "0202");
        request.put("custom_auth_code", apiKey);
        request.put("custom_auth_token", apiToken);
        request.put("dev_yn", "N");
        request.put("tr_id", trId);
        request.put("user_id", userId);

        return callApi("/cancel", request);
    }

    /**
     * Check business account balance (API Code: 0301)
     */
    public BigDecimal getAccountBalance() {
        log.info("Checking GiftiShow account balance");

        Map<String, String> request = new HashMap<>();
        request.put("api_code", "0301");
        request.put("custom_auth_code", apiKey);
        request.put("custom_auth_token", apiToken);
        request.put("dev_yn", "N");
        request.put("user_id", userId);

        Map<String, Object> response = callApi("/bizmoney", request);
        if (response != null && response.containsKey("result")) {
            Map<String, Object> result = (Map<String, Object>) response.get("result");
            Object balance = result.get("balance");
            return new BigDecimal(balance.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Resend coupon (API Code: 0203)
     */
    public Map<String, Object> resendCoupon(String trId) {
        log.info("Resending coupon: trId={}", trId);

        Map<String, String> request = new HashMap<>();
        request.put("api_code", "0203");
        request.put("custom_auth_code", apiKey);
        request.put("custom_auth_token", apiToken);
        request.put("dev_yn", "N");
        request.put("tr_id", trId);
        request.put("sms_flag", "N");
        request.put("user_id", userId);

        return callApi("/resend", request);
    }

    /**
     * Common API call method
     */
    private Map<String, Object> callApi(String endpoint, Map<String, String> request) {
        try {
            String url = API_BASE_URL + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                log.error("GiftiShow API error: status={}", response.getStatusCode());
                throw new RuntimeException("GiftiShow API call failed");
            }
        } catch (Exception e) {
            log.error("Error calling GiftiShow API: {}", e.getMessage(), e);
            throw new RuntimeException("GiftiShow API error: " + e.getMessage(), e);
        }
    }
}
