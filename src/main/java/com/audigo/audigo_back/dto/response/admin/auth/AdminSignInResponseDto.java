package com.audigo.audigo_back.dto.response.admin.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.response.ResponseDto;

import lombok.Getter;

@Getter
public class AdminSignInResponseDto extends ResponseDto {

    private String token;
    private int expirationTime;
    
    // private 생성자는 외부에서 생성 할 수 없음
    private AdminSignInResponseDto(String token) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.token = token;
        this.expirationTime = 3600;
    }

    public static ResponseEntity<AdminSignInResponseDto> success(String token) {
        AdminSignInResponseDto result = new AdminSignInResponseDto(token);
        return ResponseEntity.status(200).body(result);
    }

    public static ResponseEntity<ResponseDto> signInFail() {
        ResponseDto result = new ResponseDto(ResponseCode.SIGN_IN_FAIL, ResponseMessage.SIGN_IN_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistedAdmin() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_ADMIN, ResponseMessage.NOT_EXISTED_ADMIN);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
}
