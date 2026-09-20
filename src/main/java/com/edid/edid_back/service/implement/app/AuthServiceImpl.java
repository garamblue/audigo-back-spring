package com.audigo.audigo_back.service.implement.app;

import org.springframework.http.ResponseEntity;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;//DI

    private final JWTUtil jwtUtil;

    /**
     * 회원가입
     */
    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto) {
        String accessToken = "";
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
            
            String mobileNumb = dto.getMobileNumb();
            boolean existedMobileNumb = memberRepository.existsByMobileNumb(mobileNumb);
            if (existedMobileNumb)
                return SignUpResponseDto.duplicateMobileNumber();

            // 3시간
            accessToken = jwtUtil.createJwtWithEmail(email, 3);
            log.info("=== Member's Access Token : " + accessToken.toString());
            // 2160시간 = 90일
            String refreshToken = jwtUtil.createRefreshToken(email, nickname, mobileNumb, 2160);
            log.info("=== Member's Refresh Token : " + refreshToken.toString());

            // PostgreSQL FUNCTION 호출 및 결과 반환
            Map<String, Object> result = memberRepository.registerMember(
                dto.getEmail() != null ? dto.getEmail() : "must_have_email",
                dto.getNickname() != null ? dto.getNickname() : "must_have_nickname",
                dto.getBirthDt() != null ? dto.getBirthDt() : "",
                dto.getGender() != null ? dto.getGender() : "",
                dto.getSnsDiv() != null ? dto.getSnsDiv() : "",
                dto.getInvitationCd() != null ? dto.getInvitationCd() : "",
                dto.getInviterCd() != null ? dto.getInviterCd() : "",
                dto.getMissionYn() != null ? dto.getMissionYn() : "",
                dto.getPushTkn() != null ? dto.getPushTkn() : "",
                refreshToken,
                dto.getSnsId() != null ? dto.getSnsId() : "must_have_sns_id",
                dto.getSnsVal() != null ? dto.getSnsVal() : "",
                dto.getModel() != null ? dto.getModel() : "",
                dto.getAppVers() != null ? dto.getAppVers() : "",
                dto.getOsVers() != null ? dto.getOsVers() : "",
                dto.getOsName() != null ? dto.getOsName() : "",
                dto.getLang() != null ? dto.getLang() : "",
                dto.getMobileNumb() != null ? dto.getMobileNumb() : "00011112222",
                dto.getRegionCd() != null ? dto.getRegionCd() : "",
                dto.getPushAlive() != null ? dto.getPushAlive() : ""
            );
            
            // 반환된 데이터 사용 예시
            if (result != null) {
                log.info("=== result keys: " + result.keySet());
                
                // 컴럼명으로 직접 접근
                log.info("=== m_idx: " + result.get("m_idx"));
                log.info("=== email: " + result.get("email"));
                log.info("=== logical_id: " + result.get("logical_id"));
                log.info("=== join_dt: " + result.get("join_dt"));
                log.info("=== seq: " + result.get("seq"));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignUpResponseDto.success(accessToken);
    }

    /**
     * 로그인
     */
    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {
        String token = null;

        try {
            String email = dto.getEmail();
            log.info("=== AuthServiceImpl signIn email: " + email);

            MemberEntity memEntity = memberRepository.findByEmail(email);

            if (memEntity == null)
                return SignInResponseDto.signInFail();

            log.info("=== AuthServiceImpl memEntity : " + memEntity.toString());

            //String password = dto.getPassword();
            //String encodedPassword = userEntity.getPassword();
            //boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            //if (!isMatched)
            //    return SignInResponseDto.signInFail();

            // 1시간 = 60분 × 60초 × 1000밀리초 = 3,600,000 밀리초
            token = jwtUtil.createJwtWithEmail(email, 3);
            log.info("=== AuthServiceImpl token : " + token.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignInResponseDto.success(token);
    }

}