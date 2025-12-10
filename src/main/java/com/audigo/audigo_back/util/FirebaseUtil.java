package com.audigo.audigo_back.util;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Firebase Cloud Messaging (FCM) 푸시 알림 유틸리티
 */
@Component
@Slf4j
public class FirebaseUtil {

    /**
     * FCM 메시지 생성
     * @param title 알림 제목
     * @param body 알림 내용
     * @param tokens 푸시 토큰 리스트
     * @return Message 리스트
     */
    public List<Message> createFcmMessages(String title, String body, List<String> tokens) {
        return tokens.stream()
                .map(token -> Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setToken(token)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 여러 대상에게 FCM 메시지 전송
     * @param messages 전송할 메시지 리스트
     * @return BatchResponse
     */
    public BatchResponse sendMulticastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Empty FCM receiver list");
        }

        try {
            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            BatchResponse response = firebaseMessaging.sendEach(messages);

            log.info("FCM sent - success: {}, failed: {}",
                    response.getSuccessCount(), response.getFailureCount());

            // 실패한 메시지 로깅
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.warn("FCM failed for message {}: {}",
                                i, responses.get(i).getException().getMessage());
                    }
                }
            }

            return response;

        } catch (Exception e) {
            log.error("FCM send error", e);
            throw new RuntimeException("FCM sendAll failed", e);
        }
    }

    /**
     * 단일 회원에게 FCM 메시지 전송
     * @param token 푸시 토큰
     * @param title 알림 제목
     * @param body 알림 내용
     * @param data 추가 데이터 (선택)
     * @return 메시지 ID
     */
    public String sendToMember(String token, String title, String body, Map<String, String> data) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Push token is missing");
        }

        try {
            // 데이터 기본값 설정
            Map<String, String> messageData = data != null ? data : createDefaultData();

            // 메시지 생성
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(messageData)
                    .build();

            // 전송
            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            String messageId = firebaseMessaging.send(message);

            log.info("FCM sent successfully: {}", messageId);
            return messageId;

        } catch (Exception e) {
            log.error("Firebase messaging send error", e);
            throw new RuntimeException("FCM send failed", e);
        }
    }

    /**
     * 단일 회원에게 FCM 메시지 전송 (데이터 없이)
     */
    public String sendToMember(String token, String title, String body) {
        return sendToMember(token, title, body, null);
    }

    /**
     * 기본 데이터 맵 생성
     */
    private Map<String, String> createDefaultData() {
        Map<String, String> data = new HashMap<>();
        data.put("display", "toast");
        data.put("reward", "");
        data.put("page", "");
        data.put("value", "");
        return data;
    }

    /**
     * Object 데이터를 String 맵으로 변환
     */
    public Map<String, String> convertToStringMap(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return createDefaultData();
        }

        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            stringMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return stringMap;
    }

    /**
     * 토픽으로 FCM 메시지 전송
     * @param topic 토픽 이름
     * @param title 알림 제목
     * @param body 알림 내용
     * @return 메시지 ID
     */
    public String sendToTopic(String topic, String title, String body) {
        try {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            String messageId = firebaseMessaging.send(message);

            log.info("FCM sent to topic {}: {}", topic, messageId);
            return messageId;

        } catch (Exception e) {
            log.error("FCM topic send error", e);
            throw new RuntimeException("FCM topic send failed", e);
        }
    }

    /**
     * Android 전용 우선순위 설정 메시지 전송
     */
    public String sendHighPriorityMessage(String token, String title, String body, Map<String, String> data) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Push token is missing");
        }

        try {
            Map<String, String> messageData = data != null ? data : createDefaultData();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(messageData)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setContentAvailable(true)
                                    .build())
                            .build())
                    .build();

            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
            String messageId = firebaseMessaging.send(message);

            log.info("High priority FCM sent: {}", messageId);
            return messageId;

        } catch (Exception e) {
            log.error("High priority FCM send error", e);
            throw new RuntimeException("High priority FCM send failed", e);
        }
    }
}
