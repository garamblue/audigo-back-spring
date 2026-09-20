package com.audigo.audigo_back.service.app;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.audigo.audigo_back.dto.request.app.push.PushRequestDto;
import com.audigo.audigo_back.dto.response.app.push.PushResponseDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    public ResponseEntity<? super PushResponseDto> sendPushMessage(PushRequestDto requestDto) throws FirebaseMessagingException {
        try {
            // 푸시 알림의 제목과 내용을 설정
            Notification notification = Notification.builder()
                .setTitle(requestDto.getTitle())
                .setBody(requestDto.getBody())
                // .setImage("url-to-image.png") // 이미지 추가도 가능
                .build();

            // 메시지 객체 생성
            Message message = Message.builder()
                .setToken(requestDto.getTargetToken()) // 메시지를 보낼 특정 기기의 FCM 토큰
                .setNotification(notification)
                // .putData("score", "850") // 추가 데이터를 보낼 수도 있음
                .build();

            // FirebaseMessaging을 통해 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("=========================================");
            log.info("Successfully sent message: " + response);
            //        Successfully sent message: projects/greever-394502/messages/0:1757985804108392%e5d7c00de5d7c00d
            log.info("=========================================");
            // DB처리도 추가
        } catch (Exception e) {
            e.printStackTrace();
            return PushResponseDto.databaseError();
        }
        return PushResponseDto.success(requestDto);
    }
}
