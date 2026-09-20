package com.audigo.audigo_back.service.app;

import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.dto.request.app.auth.SignInRequestDto;
import com.audigo.audigo_back.dto.request.app.auth.SignUpRequestDto;
import com.audigo.audigo_back.dto.response.app.auth.SignInResponseDto;
import com.audigo.audigo_back.dto.response.app.auth.SignUpResponseDto;

public interface AuthService {

    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto);

    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);
}
