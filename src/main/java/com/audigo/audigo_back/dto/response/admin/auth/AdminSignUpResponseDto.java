package com.audigo.audigo_back.dto.response.admin.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.response.ResponseDto;

import lombok.Getter;

@Getter
public class AdminSignUpResponseDto extends ResponseDto {

    private AdminSignUpResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<AdminSignUpResponseDto> success() {
        AdminSignUpResponseDto result = new AdminSignUpResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> duplicateId() {
        ResponseDto result = new ResponseDto(ResponseCode.DUPLICATE_ID, ResponseMessage.DUPLICATE_ID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
