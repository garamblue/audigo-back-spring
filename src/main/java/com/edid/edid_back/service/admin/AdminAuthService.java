package com.edid.edid_back.service.admin;

import org.springframework.http.ResponseEntity;

import com.edid.edid_back.dto.response.admin.auth.AdminSignInInfoResponseDto;

import java.util.Map;

public interface AdminAuthService {
    Map<String, Object> register(String encryptedData);

    Map<String, Object> signIn(String encryptedData);

    ResponseEntity<? super AdminSignInInfoResponseDto> getAdminsInfo(String id);
}
