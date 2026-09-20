package com.edid.edid_back.service.app;

import org.springframework.http.ResponseEntity;

import com.edid.edid_back.dto.request.app.auth.SignInRequestDto;
import com.edid.edid_back.dto.request.app.auth.SignUpRequestDto;
import com.edid.edid_back.dto.response.app.auth.SignInResponseDto;
import com.edid.edid_back.dto.response.app.auth.SignUpResponseDto;

public interface AuthService {

    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto);

    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);
}
