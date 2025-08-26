package com.audigo.audigo_back.service.admin;

import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.dto.request.admin.auth.AdminSignUpRequestDto;
import com.audigo.audigo_back.dto.request.admin.auth.AdminSignInRequestDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInInfoResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignUpResponseDto;

public interface AdminAuthService {
    ResponseEntity<? super AdminSignUpResponseDto> register(AdminSignUpRequestDto dto);

    ResponseEntity<? super AdminSignInResponseDto> signIn(AdminSignInRequestDto dto);

    ResponseEntity<? super AdminSignInInfoResponseDto> getAdminsInfo(String id);
}
