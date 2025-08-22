package com.audigo.audigo_back.service.implement.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.audigo.audigo_back.dto.request.admin.auth.AdminSignInRequestDto;
import com.audigo.audigo_back.dto.request.admin.auth.AdminSignUpRequestDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignUpResponseDto;
import com.audigo.audigo_back.entity.AdminEntity;
import com.audigo.audigo_back.jwt.JWTUtil;
import com.audigo.audigo_back.repository.admin.AdminRepository;
import com.audigo.audigo_back.service.admin.AdminAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService{
    private static final Log logger = LogFactory.getLog(AdminAuthServiceImpl.class);

    private final AdminRepository adminRepository;//DI

    private final JWTUtil jwtUtil;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseEntity<? super AdminSignUpResponseDto> register(AdminSignUpRequestDto dto) {
        try {
            // not null 이나 duplicate check 진행
            String id = dto.getId();
            boolean existedId = adminRepository.existsById(id);
            if (existedId)
                return AdminSignUpResponseDto.duplicateId();

            //password encode
            String password = dto.getPwd();
            String encodedPwd = passwordEncoder.encode(password);
            dto.setPwd(encodedPwd);

            //save
            AdminEntity adminEntity = new AdminEntity(dto);
            adminRepository.save(adminEntity);

        } catch (Exception e) {
            e.printStackTrace();
            return AdminSignUpResponseDto.duplicateId();
        }

        return AdminSignUpResponseDto.success();
    }

    @Override
    public ResponseEntity<? super AdminSignInResponseDto> signIn(AdminSignInRequestDto dto) {
        String token = null;

        try {
            String id = dto.getId();
            AdminEntity adminEntity = adminRepository.findById(id);

            if (adminEntity == null)
                return AdminSignInResponseDto.notExistedAdmin();

            String encodedPwd = adminEntity.getPwd();
            String password = dto.getPwd();

            String encdPwd = passwordEncoder.encode(password);
            logger.info("=== matches : (" + encdPwd + " : " + encodedPwd + " )");
            
            boolean isMatched = passwordEncoder.matches(password, encodedPwd);

            if (!isMatched)
                return AdminSignInResponseDto.signInFail();
            
            // 1시간 = 60분 × 60초 × 1000밀리초 = 3,600,000 밀리초
            token = jwtUtil.createJwtWithId(id, 3600000L);
            logger.info("=== Admin JWToken : " + token.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return AdminSignInResponseDto.databaseError();
        }
        return AdminSignInResponseDto.success(token);
    }
    
}