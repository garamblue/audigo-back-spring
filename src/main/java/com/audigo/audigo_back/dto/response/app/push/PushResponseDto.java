package com.audigo.audigo_back.dto.response.app.push;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.request.app.push.PushRequestDto;
import com.audigo.audigo_back.dto.response.ResponseDto;

import lombok.Getter;

@Getter
public class PushResponseDto extends ResponseDto {

    private String title;
    private String body;

    public PushResponseDto(PushRequestDto pushRequestDto) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.title = pushRequestDto.getTitle();
        this.body = pushRequestDto.getBody();
    }

    public static ResponseEntity<PushResponseDto> success(PushRequestDto pushRequestDto) {
        PushResponseDto result = new PushResponseDto(pushRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
}
