package com.audigo.audigo_back.controller.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audigo.audigo_back.dto.request.app.push.PushRequestDto;
import com.audigo.audigo_back.dto.response.app.push.PushResponseDto;
import com.audigo.audigo_back.service.app.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Push message API", description = "push message 전송 API")
@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * push message 전송
     * @param requestDto
     * @return
     */
    @Operation(summary = "push message send", description = "사용자에게 push message 를 보냅니다. audigo-test-app-firebase-*** 형식의 json 파일을 firebase에서 생성해야합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "전송성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "targetToken", description = "메시지를 받을 기기의 FCM 토큰", example = "token12345"),
        @Parameter(name = "title", description = "알림 제목", example = "push title 입니다."),
        @Parameter(name = "body", description = "알림 내용", example = "push contents 내용입니다.")
    })
    @PostMapping("/send")
    public ResponseEntity<? super PushResponseDto> sendPushNotification(@RequestBody PushRequestDto requestDto) {
        try {
            ResponseEntity<? super PushResponseDto> response = notificationService.sendPushMessage(requestDto);
            return response;
        } catch (Exception e) {
            log.error("Push notification 전송 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Push notification 전송 실패: " + e.getMessage());
        }
    }
    
}
