package com.audigo.audigo_back.dto.request.app.push;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PushRequestDto {
    // 메시지를 받을 기기의 FCM 등록 토큰
    @NotBlank
    private String targetToken;
    // 알림 제목
    @NotBlank
    private String title;
    // 알림 내용
    @NotBlank   
    private String body;
}
