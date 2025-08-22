package com.audigo.audigo_back.service.implement.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.audigo.audigo_back.dto.request.app.auth.SignInRequestDto;
import com.audigo.audigo_back.dto.request.app.auth.SignUpRequestDto;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.dto.response.app.auth.SignInResponseDto;
import com.audigo.audigo_back.dto.response.app.auth.SignUpResponseDto;
import com.audigo.audigo_back.entity.MemberEntity;
import com.audigo.audigo_back.jwt.JWTUtil;
import com.audigo.audigo_back.repository.app.MemberRepository;
import com.audigo.audigo_back.service.app.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Log logger = LogFactory.getLog(AuthServiceImpl.class);

    private final MemberRepository memberRepository;//DI

    // private final UserRepository userRepository;
    // private final JwtProvider jwtProvider;
    private final JWTUtil jwtUtil;

    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto) {
        try {
            // not null 이나 duplicate check 진행
            String email = dto.getEmail();
            boolean existedEmail = memberRepository.existsByEmail(email); //userRepository.existsByEmail(email);
            if (existedEmail)
                return SignUpResponseDto.duplicateEmail();

            String nickname = dto.getNickname();
            boolean existedNickname = memberRepository.existsByNickname(nickname); //userRepository
            if (existedNickname)
                return SignUpResponseDto.duplicateNickname();
            /*
            String telNumber = dto.getTelNumber();
            boolean existedTelnumber = userRepository.existsByTelNumber(telNumber);
            if (existedTelnumber)
                return SignUpResponseDto.duplicateTelNumber();

            UserEntity userEntity = new UserEntity(dto);
            userRepository.save(userEntity);
            */
            // PostgreSQL FUNCTION 호출 및 결과 반환
            Map<String, Object> result = memberRepository.registerMember(
                dto.getEmail() != null ? dto.getEmail() : "",
                dto.getNickname() != null ? dto.getNickname() : "",
                dto.getBirthDt() != null ? dto.getBirthDt() : "",
                dto.getGender() != null ? dto.getGender() : "",
                dto.getSnsDiv() != null ? dto.getSnsDiv() : "",
                dto.getInvitationCd() != null ? dto.getInvitationCd() : "",
                dto.getInviterCd() != null ? dto.getInviterCd() : "",
                dto.getMissionYn() != null ? dto.getMissionYn() : "",
                dto.getPushTkn() != null ? dto.getPushTkn() : "",
                dto.getSnsVal() != null ? dto.getSnsVal() : "",
                dto.getModel() != null ? dto.getModel() : "",
                dto.getAppVers() != null ? dto.getAppVers() : "",
                dto.getOsVers() != null ? dto.getOsVers() : "",
                dto.getOsName() != null ? dto.getOsName() : "",
                dto.getDupliTkn() != null ? dto.getDupliTkn() : "",
                dto.getLang() != null ? dto.getLang() : "",
                dto.getMobileNumb() != null ? dto.getMobileNumb() : "",
                dto.getRegionCd() != null ? dto.getRegionCd() : "",
                dto.getPushAlive() != null ? dto.getPushAlive() : ""
            );
            
            logger.info("=== registerMember result: " + result);
            
            // 반환된 데이터 사용 예시
            if (result != null) {
                logger.info("=== result keys: " + result.keySet());
                
                // 컴럼명으로 직접 접근
                logger.info("=== m_idx: " + result.get("m_idx"));
                logger.info("=== email: " + result.get("email"));
                logger.info("=== logical_id: " + result.get("logical_id"));
                logger.info("=== join_dt: " + result.get("join_dt"));
                logger.info("=== seq: " + result.get("seq"));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignUpResponseDto.success();
    }

    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {
        String token = null;

        try {
            String email = dto.getEmail();
            logger.info("=== AuthServiceImpl signIn email: " + email);

            MemberEntity memEntity = memberRepository.findByEmail(email);

            if (memEntity == null)
                return SignInResponseDto.signInFail();

            logger.info("=== AuthServiceImpl memEntity : " + memEntity.toString());

            //String password = dto.getPassword();
            //String encodedPassword = userEntity.getPassword();
            //boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            //if (!isMatched)
            //    return SignInResponseDto.signInFail();

            // 1시간 = 60분 × 60초 × 1000밀리초 = 3,600,000 밀리초
            token = jwtUtil.createJwtWithEmail(email, 3600000L);
            logger.info("=== AuthServiceImpl token : " + token.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignInResponseDto.success(token);
    }

}