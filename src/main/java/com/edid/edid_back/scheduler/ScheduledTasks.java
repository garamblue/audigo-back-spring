package com.audigo.audigo_back.scheduler;

import com.audigo.audigo_back.entity.gamification.HoroscopeDailyEntity;
import com.audigo.audigo_back.entity.scheduler.ExchangeRatioEntity;
import com.audigo.audigo_back.repository.gamification.HoroscopeDailyRepository;
import com.audigo.audigo_back.repository.scheduler.ExchangeRatioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 스케줄러 작업
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final ExchangeRatioRepository exchangeRatioRepository;
    private final HoroscopeDailyRepository horoscopeDailyRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${currency.api.key:}")
    private String currencyApiKey;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    /**
     * USD -> KRW 환율 업데이트
     * 매 8시간마다 실행 (0시, 8시, 16시)
     */
    @Scheduled(cron = "0 0 */8 * * *")
    public void updateUsdExchangeRate() {
        log.info("Starting USD exchange rate update");

        try {
            String url = String.format(
                    "https://api.currencyapi.com/v3/latest?apikey=%s&base_currency=USD&currencies=KRW",
                    currencyApiKey
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode krwNode = root.path("data").path("KRW").path("value");

                if (!krwNode.isMissingNode()) {
                    BigDecimal krwRatio = new BigDecimal(krwNode.asText());

                    ExchangeRatioEntity entity = new ExchangeRatioEntity();
                    entity.setUsRatio(krwRatio);
                    exchangeRatioRepository.save(entity);

                    log.info("USD/KRW exchange rate updated: {}", krwRatio);
                } else {
                    log.error("KRW data not found in response");
                }
            } else {
                log.error("Failed to fetch exchange rate: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error updating USD exchange rate", e);
        }
    }

    /**
     * 토큰 가격 업데이트
     * 매 5분마다 실행
     * TODO: 실제 토큰 가격 API 연동 필요
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void updateTokenPrice() {
        log.info("Token price update (placeholder)");
        // TODO: 토큰 가격 업데이트 로직 구현
    }

    /**
     * 일일 운세 생성
     * 매일 12시 1분에 실행
     */
    @Scheduled(cron = "0 1 12 * * *")
    public void generateDailyHoroscope() {
        log.info("Starting daily horoscope generation");

        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            log.warn("OpenAI API key not configured, skipping horoscope generation");
            return;
        }

        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);

            // OpenAI API 호출 (GPT-4)
            String[] westernSignsKo = {"양자리", "황소자리", "쌍둥이자리", "게자리", "사자자리", "처녀자리",
                                       "천칭자리", "전갈자리", "사수자리", "염소자리", "물병자리", "물고기자리"};
            String[] westernSignsEn = {"Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
                                       "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"};
            String[] easternSignsKo = {"쥐", "소", "호랑이", "토끼", "용", "뱀", "말", "양", "원숭이", "닭", "개", "돼지"};
            String[] easternSignsEn = {"Rat", "Ox", "Tiger", "Rabbit", "Dragon", "Snake",
                                       "Horse", "Goat", "Monkey", "Rooster", "Dog", "Pig"};

            String prompt = String.format(
                "오늘(%s) 기준 '오늘의 종합 운세'를 생성해. " +
                "서양 12별자리와 동양 12띠 각각에 대해 한국어(ko)와 영어(en)로 작성. " +
                "각 운세는 한글 기준 300-400자 범위, 최소 8문장 이상. " +
                "미신적 표현 피하고 현실적인 조언 중심으로 작성.",
                tomorrow
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4");
            requestBody.put("temperature", 0.6);

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            requestBody.put("messages", new Object[]{message});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String content = root.path("choices").get(0).path("message").path("content").asText();

                // JSON 파싱하여 운세 데이터 저장
                JsonNode horoscopeData = objectMapper.readTree(content);

                // 서양 별자리 저장
                for (int i = 0; i < westernSignsKo.length; i++) {
                    saveHoroscope(tomorrow, westernSignsKo[i], "Z", "KO",
                        horoscopeData.path("western").path("ko").path(westernSignsKo[i]).asText());
                    saveHoroscope(tomorrow, westernSignsEn[i], "Z", "EN",
                        horoscopeData.path("western").path("en").path(westernSignsEn[i]).asText());
                }

                // 동양 띠 저장
                for (int i = 0; i < easternSignsKo.length; i++) {
                    saveHoroscope(tomorrow, easternSignsKo[i], "A", "KO",
                        horoscopeData.path("eastern").path("ko").path(easternSignsKo[i]).asText());
                    saveHoroscope(tomorrow, easternSignsEn[i], "A", "EN",
                        horoscopeData.path("eastern").path("en").path(easternSignsEn[i]).asText());
                }

                log.info("Daily horoscope generated successfully for {}", tomorrow);
            }

        } catch (Exception e) {
            log.error("Error generating daily horoscope", e);
        }
    }

    private void saveHoroscope(LocalDate date, String sign, String type, String lang, String contents) {
        if (contents == null || contents.isEmpty()) {
            log.warn("Empty horoscope content for {} {} {}", date, sign, lang);
            return;
        }

        HoroscopeDailyEntity entity = new HoroscopeDailyEntity();
        entity.setDate(date);
        entity.setSign(sign);
        entity.setTp(type);
        entity.setLang(lang);
        entity.setContents(contents);
        horoscopeDailyRepository.save(entity);
    }

    /**
     * 기프티쇼 데이터 업데이트
     * 매일 15시 5분에 실행
     */
    @Scheduled(cron = "0 5 15 * * *")
    public void updateGiftishow() {
        log.info("Giftishow update (placeholder)");
        // TODO: 기프티쇼 API 연동 구현
    }

    /**
     * FCM 푸시 알림
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void sendFcmNotifications() {
        log.info("FCM push notifications (placeholder)");
        // TODO: FCM 푸시 알림 로직 구현
    }

    /**
     * 보상 만료 처리
     * 매월 1일 15시 2분에 실행
     */
    @Scheduled(cron = "0 2 15 1 * *")
    public void expireRewards() {
        log.info("Reward expiration (placeholder)");
        // TODO: 보상 만료 처리 로직 구현
    }

    /**
     * 랭킹 집계
     * 매월 1일 15시 8분에 실행
     */
    @Scheduled(cron = "0 8 15 1 * *")
    public void calculateRanking() {
        log.info("Ranking calculation (placeholder)");
        // TODO: 랭킹 집계 로직 구현
    }
}
