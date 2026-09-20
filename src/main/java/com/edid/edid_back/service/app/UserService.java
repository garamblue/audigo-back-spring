package com.edid.edid_back.service.app;

import org.springframework.http.ResponseEntity;

import com.edid.edid_back.dto.response.app.user.GetSignInUserResponseDto;

public interface UserService {

    ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String email);

}