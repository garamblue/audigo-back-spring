package com.audigo.audigo_back.service.implement.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.audigo.audigo_back.dto.request.admin.auth.AdminSignInRequestDto;
import com.audigo.audigo_back.dto.request.admin.auth.AdminSignUpRequestDto;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInInfoResponseDto;
import com.audigo.audigo_back.entity.AdminEntity;
import com.audigo.audigo_back.jwt.JWTUtil;
import com.audigo.audigo_back.repository.admin.AdminRepository;
import com.audigo.audigo_back.service.admin.AdminAuthService;
import com.audigo.audigo_back.util.AesUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService{

    private final AdminRepository adminRepository;
    private final JWTUtil jwtUtil;
    private final AesUtil aesUtil;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, Object> register(String encryptedData) {
        try {
            // 복호화
            AdminSignUpRequestDto dto = aesUtil.decryptAdmin(encryptedData, AdminSignUpRequestDto.class);

            // Validation
            if (dto.getId() == null || dto.getPwd() == null || dto.getNm() == null) {
                throw new IllegalArgumentException("Required fields are missing");
            }

            // duplicate check
            String id = dto.getId();
            boolean existedId = adminRepository.existsById(id);
            if (existedId) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", "0");
                response.put("msg", "Duplicate ID");
                return response;
            }

            // password encode
            String password = dto.getPwd();
            String encodedPwd = passwordEncoder.encode(password);
            dto.setPwd(encodedPwd);

            // save
            AdminEntity adminEntity = new AdminEntity(dto);
            adminRepository.save(adminEntity);

            Map<String, Object> response = new HashMap<>();
            response.put("code", "1");
            response.put("msg", "success");
            return response;

        } catch (Exception e) {
            log.error("Admin register error", e);
            throw new RuntimeException("Admin register failed", e);
        }
    }

    @Override
    public Map<String, Object> signIn(String encryptedData) {
        try {
            // 복호화
            AdminSignInRequestDto dto = aesUtil.decryptAdmin(encryptedData, AdminSignInRequestDto.class);

            // Validation
            if (dto.getId() == null || dto.getPwd() == null) {
                throw new IllegalArgumentException("Required fields are missing");
            }

            String id = dto.getId();
            AdminEntity adminEntity = adminRepository.findById(id);

            if (adminEntity == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", "0");
                response.put("msg", "Admin not found");
                return response;
            }

            String encodedPwd = adminEntity.getPwd();
            String password = dto.getPwd();

            boolean isMatched = passwordEncoder.matches(password, encodedPwd);

            if (!isMatched) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", "0");
                response.put("msg", "Sign in failed");
                return response;
            }

            // 1시간 = 60분 × 60초 × 1000밀리초 = 3,600,000 밀리초
            String token = jwtUtil.createJwtWithId(id, 3600000L);
            log.info("=== Admin JWToken : " + token);

            Map<String, Object> response = new HashMap<>();
            response.put("code", "1");
            response.put("msg", "success");
            response.put("accessToken", token);
            response.put("id", id);
            response.put("nm", adminEntity.getNm());
            return response;

        } catch (Exception e) {
            log.error("Admin sign in error", e);
            throw new RuntimeException("Admin sign in failed", e);
        }
    }

    /**
     * 로그인한 admin의 정보를 가져옴
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<? super AdminSignInInfoResponseDto> getAdminsInfo(String id) {
        AdminEntity adminEntity = null;

        try {
            adminEntity = adminRepository.findById(id);
            if (adminEntity == null)
                return AdminSignInInfoResponseDto.notExistingAdmin();
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return AdminSignInInfoResponseDto.success(adminEntity);
    }
    
}