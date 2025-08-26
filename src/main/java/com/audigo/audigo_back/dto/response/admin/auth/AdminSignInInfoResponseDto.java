package com.audigo.audigo_back.dto.response.admin.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.entity.AdminEntity;

import lombok.Getter;

@Getter
public class AdminSignInInfoResponseDto extends ResponseDto {

    private String id;
    private String nm;
    private String mobile;

    public AdminSignInInfoResponseDto(AdminEntity adminEntity) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.id = adminEntity.getId();
        this.nm = adminEntity.getNm();
        this.mobile = adminEntity.getMobile();
    }
    
    public static ResponseEntity<AdminSignInInfoResponseDto> success(AdminEntity adminEntity) {
        AdminSignInInfoResponseDto responseBody = new AdminSignInInfoResponseDto(adminEntity);
        return ResponseEntity.status(200).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> notExistingAdmin() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NOT_EXISTED_ADMIN, ResponseMessage.NOT_EXISTED_ADMIN);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

}