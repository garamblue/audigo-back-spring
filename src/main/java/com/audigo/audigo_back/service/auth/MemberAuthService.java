package com.audigo.audigo_back.service.auth;

import com.audigo.audigo_back.entity.member.*;
import com.audigo.audigo_back.entity.reward.RewardBalanceEntity;
import com.audigo.audigo_back.entity.skin.SkinExchangeEntity;
import com.audigo.audigo_back.entity.terms.TermsAgreedEntity;
import com.audigo.audigo_back.entity.terms.TermsConditionsEntity;
import com.audigo.audigo_back.entity.web3.EWalletEntity;
import com.audigo.audigo_back.repository.member.*;
import com.audigo.audigo_back.repository.reward.RewardBalanceRepository;
import com.audigo.audigo_back.repository.skin.SkinExchangeRepository;
import com.audigo.audigo_back.repository.terms.TermsAgreedRepository;
import com.audigo.audigo_back.repository.terms.TermsConditionsRepository;
import com.audigo.audigo_back.repository.web3.EWalletRepository;
import com.audigo.audigo_back.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 회원 인증 서비스 (회원가입, 로그인)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAuthService {

    private final MembersRepository membersRepository;
    private final MembersSnsRepository membersSnsRepository;
    private final MembersTimezoneRepository membersTimezoneRepository;
    private final MembersLogRepository membersLogRepository;
    private final MembersSessionRepository membersSessionRepository;
    private final MembersGradeRepository membersGradeRepository;
    private final MembersInvitationRepository membersInvitationRepository;
    private final RewardBalanceRepository rewardBalanceRepository;
    private final EWalletRepository eWalletRepository;
    private final SkinExchangeRepository skinExchangeRepository;
    private final TermsConditionsRepository termsConditionsRepository;
    private final TermsAgreedRepository termsAgreedRepository;

    private final AesUtil aesUtil;
    private final JwtUtil jwtUtil;
    private final CodeUtil codeUtil;
    private final DateUtil dateUtil;

    /**
     * 회원가입
     */
    @Transactional
    public Map<String, Object> signUp(String encryptedData) {
        try {
            // 1. 복호화
            Map<String, Object> data = aesUtil.decryptMember(encryptedData, Map.class);

            String email = (String) data.get("email");
            String nickname = (String) data.get("nickname");
            String birthDtStr = (String) data.get("birth_dt");
            String gender = (String) data.get("gender");
            String mobileNum = (String) data.get("mobile_num");
            String state = (String) data.get("state");
            String snsDiv = (String) data.get("sns_div");
            String snsId = (String) data.get("sns_id");
            String regionCd = (String) data.get("region_cd");
            String mobileTz = (String) data.get("mobile_tz");
            String ip = (String) data.get("ip");
            String os = (String) data.get("os");
            String osVers = (String) data.get("os_vers");
            String appVers = (String) data.get("app_vers");
            String deviceInfo = (String) data.get("device_info");
            String deviceId = (String) data.get("device_id");
            String pushTkn = (String) data.get("push_tkn");
            String invitCd = (String) data.get("invit_cd");
            Map<String, String> terms = (Map<String, String>) data.get("terms");

            LocalDate birthDt = LocalDate.parse(birthDtStr);
            String snsVal = snsDiv + "_" + snsId;

            // 2. 중복 회원 확인
            List<MembersSnsEntity> existingMembers = membersSnsRepository.findBySnsAccount(snsDiv, snsVal);
            if (!existingMembers.isEmpty()) {
                MembersEntity member = membersRepository.findById(existingMembers.get(0).getMIdx()).orElse(null);
                if (member != null && member.getLvDt() != null) {
                    // 탈퇴 회원인 경우 1개월 제한 확인
                    if (dateUtil.isWithinOneMonth(member.getLvDt().toLocalDate())) {
                        throw new IllegalStateException("탈퇴한 회원입니다. 탈퇴 후 1개월 이내에는 재가입이 불가능합니다.");
                    }
                } else {
                    throw new IllegalStateException("이미 가입된 회원입니다. 다른 SNS 또는 이메일로 가입해 주세요.");
                }
            }

            // 3. 회원 생성
            MembersEntity newMember = new MembersEntity();
            newMember.setStts(dateUtil.isAdult(birthDt) ? "1" : "4"); // 1=일반, 4=미성년자
            newMember.setEmail(email);
            newMember.setNickname(nickname);
            newMember.setBirthDt(birthDt);
            newMember.setGender(gender);
            newMember.setState(state != null ? state : "11");
            newMember.setMobileNum(mobileNum);
            newMember.setInvitCd(codeUtil.generateRandomCode(12));
            newMember.setExtKey(codeUtil.generateRandomCode(16));
            newMember.setCdt(LocalDateTime.now());

            newMember = membersRepository.save(newMember);
            BigInteger mIdx = newMember.getMIdx();

            // 4. SNS 연동 생성
            MembersSnsEntity membersSns = new MembersSnsEntity();
            membersSns.setMIdx(mIdx);
            membersSns.setSnsDiv(snsDiv);
            membersSns.setSnsVal(snsVal);
            membersSns.setCdt(LocalDateTime.now());
            membersSnsRepository.save(membersSns);

            // 5. 타임존 생성
            MembersTimezoneEntity timezone = new MembersTimezoneEntity();
            timezone.setMIdx(mIdx);
            timezone.setMobileTz(mobileTz);
            timezone.setRegionCd(regionCd);
            timezone.setStts("Y");
            timezone.setAprvDt(LocalDateTime.now());
            timezone.setCdt(LocalDateTime.now());
            membersTimezoneRepository.save(timezone);

            // 6. 로그인 이력 생성
            MembersLogEntity loginLog = new MembersLogEntity();
            loginLog.setMIdx(mIdx);
            loginLog.setIp(ip);
            loginLog.setOs(os);
            loginLog.setOsVers(osVers);
            loginLog.setAppVers(appVers);
            loginLog.setDeviceInfo(deviceInfo);
            loginLog.setCdt(LocalDateTime.now());
            membersLogRepository.save(loginLog);

            // 7. JWT 토큰 생성 및 세션 생성
            Map<String, Object> jwtPayload = new HashMap<>();
            jwtPayload.put("sns_val", snsVal);
            jwtPayload.put("nickname", nickname);
            jwtPayload.put("push_tkn", pushTkn);

            String refreshToken = jwtUtil.generateMemberRefreshToken(jwtPayload);
            String accessToken = jwtUtil.generateMemberAccessToken(jwtPayload);

            MembersSessionEntity session = new MembersSessionEntity();
            session.setMIdx(mIdx);
            session.setRefreshTkn(refreshToken);
            session.setDeviceId(deviceId);
            session.setPushTkn(pushTkn);
            session.setTokenExdt(LocalDateTime.now().plusDays(30));
            session.setExpired("N");
            session.setCdt(LocalDateTime.now());
            membersSessionRepository.save(session);

            // 8. 보상 잔액 초기화
            RewardBalanceEntity rewardBalance = new RewardBalanceEntity();
            rewardBalance.setMIdx(mIdx);
            rewardBalance.setSumAmt(BigDecimal.ZERO);
            rewardBalanceRepository.save(rewardBalance);

            // 9. 회원 등급 생성 (기본 스킨 슬롯 10개)
            MembersGradeEntity grade = new MembersGradeEntity();
            grade.setMIdx(mIdx);
            grade.setSkinSize(10);
            membersGradeRepository.save(grade);

            // 10. 기본 스킨 생성 (sl_idx = 12478)
            SkinExchangeEntity skin = new SkinExchangeEntity();
            skin.setMIdx(mIdx);
            skin.setSlIdx(12478L);
            skin.setKeepYn("Y");
            skin.setOrdr(1);
            skin.setCdt(LocalDateTime.now());
            skinExchangeRepository.save(skin);

            // 11. 전자지갑 생성
            EWalletEntity wallet = new EWalletEntity();
            wallet.setMIdx(mIdx);
            wallet.setAddr(""); // 지갑 주소는 나중에 생성
            wallet.setServerKey(codeUtil.generateSalt(32));
            wallet.setTokenAmt(BigDecimal.ZERO);
            wallet.setBnbAmt(BigDecimal.ZERO);
            eWalletRepository.save(wallet);

            // 12. 초대 코드 처리
            if (invitCd != null && !invitCd.isEmpty()) {
                MembersEntity inviter = membersRepository.findByInvitCd(invitCd)
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

                Long inviteCount = membersInvitationRepository.countByInviterMidx(inviter.getMIdx());
                if (inviteCount >= 30) {
                    throw new IllegalStateException("초대 가능한 횟수를 초과했습니다. (최대 30명)");
                }

                MembersInvitationEntity invitation = new MembersInvitationEntity();
                invitation.setInviterMidx(inviter.getMIdx());
                invitation.setMIdx(mIdx);
                invitation.setCdt(LocalDateTime.now());
                membersInvitationRepository.save(invitation);
            }

            // 13. 약관 동의 처리
            saveTermsAgreements(mIdx, regionCd, terms);

            // 14. 응답 데이터 생성
            Map<String, Object> response = new HashMap<>();
            response.put("refresh_tkn", refreshToken);
            response.put("access_tkn", accessToken);

            Map<String, Object> info = new HashMap<>();
            info.put("sns", snsDiv);
            info.put("stts", newMember.getStts());
            info.put("nickname", newMember.getNickname());
            info.put("birth_dt", newMember.getBirthDt().toString());
            info.put("gender", newMember.getGender());
            info.put("invit_cd", newMember.getInvitCd());
            info.put("mobile_num", newMember.getMobileNum());
            info.put("ext_key", newMember.getExtKey());
            info.put("region_cd", timezone.getRegionCd());
            info.put("mobile_tz", timezone.getMobileTz());
            info.put("wallet_addr", "");
            info.put("cdt", newMember.getCdt().toString());

            response.put("info", info);

            log.info("회원가입 완료: mIdx={}, nickname={}", mIdx, nickname);

            return response;

        } catch (Exception e) {
            log.error("회원가입 실패", e);
            throw new RuntimeException("회원가입에 실패했습니다.", e);
        }
    }

    /**
     * 약관 동의 저장
     */
    private void saveTermsAgreements(BigInteger mIdx, String regionCd, Map<String, String> terms) {
        if (terms == null || terms.isEmpty()) {
            throw new IllegalArgumentException("약관 동의 정보가 필요합니다.");
        }

        String[] termTypes = {"T0001", "T0002", "T0003", "T0004", "T0005", "T0006"};
        String[] termKeys = {"terms", "privacy", "push", "night", "email", "web3"};

        for (int i = 0; i < termKeys.length; i++) {
            final int index = i; // final 변수로 복사
            String agreed = terms.get(termKeys[index]);
            if (agreed == null) {
                continue;
            }

            TermsConditionsEntity termsCondition = termsConditionsRepository
                    .findLatestByTypeAndRegion(termTypes[index], regionCd)
                    .orElseThrow(() -> new IllegalStateException("약관을 찾을 수 없습니다: " + termTypes[index]));

            TermsAgreedEntity termsAgreed = new TermsAgreedEntity();
            termsAgreed.setMIdx(mIdx);
            termsAgreed.setTncIdx(termsCondition.getTncIdx());
            termsAgreed.setAgreed(agreed);
            termsAgreed.setCdt(LocalDateTime.now());
            termsAgreedRepository.save(termsAgreed);
        }
    }

    /**
     * 로그인
     * @return Map with code: "1"=로그인 성공, "2"=중복 계정, "3"=신규 가입 필요
     */
    @Transactional
    public Map<String, Object> signIn(String encryptedData) {
        try {
            // 1. 복호화
            Map<String, Object> data = aesUtil.decryptMember(encryptedData, Map.class);

            String snsDiv = (String) data.get("sns_div");
            String snsId = (String) data.get("sns_id");
            String email = (String) data.get("email");
            String mobileNum = (String) data.get("mobile_num");
            String birthDtStr = (String) data.get("birth_dt");
            String ip = (String) data.get("ip");
            String os = (String) data.get("os");
            String osVers = (String) data.get("os_vers");
            String appVers = (String) data.get("app_vers");
            String deviceInfo = (String) data.get("device_info");
            String deviceId = (String) data.get("device_id");
            String pushTkn = (String) data.get("push_tkn");

            LocalDate birthDt = LocalDate.parse(birthDtStr);
            String snsVal = snsDiv + "_" + snsId;

            // 2. SNS 계정으로 회원 확인
            List<MembersSnsEntity> snsList = membersSnsRepository.findBySnsAccount(snsDiv, snsVal);

            if (!snsList.isEmpty()) {
                // 로그인 처리
                return processLogin(snsList.get(0).getMIdx(), snsVal, ip, os, osVers, appVers, deviceInfo, deviceId, pushTkn);
            }

            // 3. 이메일 + 휴대폰번호/생년월일로 회원 확인
            List<MembersEntity> existingMembers = membersRepository.findByEmailAndMobileOrBirth(email, mobileNum, birthDt);

            if (!existingMembers.isEmpty()) {
                // 중복 계정 (다른 SNS로 가입된 계정)
                String snsDivList = membersSnsRepository.findSnsListByMIdx(existingMembers.get(0).getMIdx())
                        .orElse("");

                Map<String, Object> response = new HashMap<>();
                response.put("code", "2");
                response.put("sns", snsDivList);
                return response;
            }

            // 4. 신규 가입 필요
            Map<String, Object> response = new HashMap<>();
            response.put("code", "3");
            return response;

        } catch (Exception e) {
            log.error("로그인 실패", e);
            throw new RuntimeException("로그인에 실패했습니다.", e);
        }
    }

    /**
     * 로그인 처리 (JWT 생성, 세션 관리, 잔액 조회)
     */
    private Map<String, Object> processLogin(BigInteger mIdx, String snsVal, String ip, String os,
                                               String osVers, String appVers, String deviceInfo,
                                               String deviceId, String pushTkn) {
        // 1. 회원 정보 조회
        MembersEntity member = membersRepository.findById(mIdx)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        // 2. 타임존 조회
        MembersTimezoneEntity timezone = membersTimezoneRepository.findActiveByMIdx(mIdx)
                .orElseThrow(() -> new IllegalStateException("타임존 정보를 찾을 수 없습니다."));

        // 3. SNS 목록 조회
        String snsDivList = membersSnsRepository.findSnsListByMIdx(mIdx).orElse("");

        // 4. 로그인 이력 저장
        MembersLogEntity loginLog = new MembersLogEntity();
        loginLog.setMIdx(mIdx);
        loginLog.setIp(ip);
        loginLog.setOs(os);
        loginLog.setOsVers(osVers);
        loginLog.setAppVers(appVers);
        loginLog.setDeviceInfo(deviceInfo);
        loginLog.setCdt(LocalDateTime.now());
        membersLogRepository.save(loginLog);

        // 5. 보상 잔액 조회
        RewardBalanceEntity rewardBalance = rewardBalanceRepository.findById(mIdx)
                .orElseThrow(() -> new IllegalStateException("보상 잔액 정보를 찾을 수 없습니다."));

        // 6. 지갑 정보 조회
        EWalletEntity wallet = eWalletRepository.findByMIdx(mIdx)
                .orElse(null);

        String walletAddr = wallet != null ? wallet.getAddr() : "";
        BigDecimal tokenAmt = wallet != null ? wallet.getTokenAmt() : BigDecimal.ZERO;
        BigDecimal bnbAmt = wallet != null ? wallet.getBnbAmt() : BigDecimal.ZERO;

        // 7. 세션 확인 및 생성
        String refreshToken;
        String accessToken;

        Map<String, Object> jwtPayload = new HashMap<>();
        jwtPayload.put("sns_val", snsVal);
        jwtPayload.put("nickname", member.getNickname());
        jwtPayload.put("push_tkn", pushTkn);

        accessToken = jwtUtil.generateMemberAccessToken(jwtPayload);

        MembersSessionEntity existingSession = membersSessionRepository.findByDeviceAndToken(mIdx, deviceId, pushTkn)
                .orElse(null);

        if (existingSession == null) {
            // 기존 세션 모두 만료
            membersSessionRepository.expireAllSessions(mIdx);

            // 새 세션 생성
            refreshToken = jwtUtil.generateMemberRefreshToken(jwtPayload);

            MembersSessionEntity newSession = new MembersSessionEntity();
            newSession.setMIdx(mIdx);
            newSession.setRefreshTkn(refreshToken);
            newSession.setDeviceId(deviceId);
            newSession.setPushTkn(pushTkn);
            newSession.setTokenExdt(LocalDateTime.now().plusDays(30));
            newSession.setExpired("N");
            newSession.setCdt(LocalDateTime.now());
            membersSessionRepository.save(newSession);
        } else {
            refreshToken = existingSession.getRefreshTkn();
        }

        // 8. 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("code", "1");
        response.put("access_tkn", accessToken);
        response.put("refresh_tkn", refreshToken);

        Map<String, Object> info = new HashMap<>();
        info.put("sns", snsDivList);
        info.put("stts", member.getStts());
        info.put("nickname", member.getNickname());
        info.put("birth_dt", member.getBirthDt().toString());
        info.put("gender", member.getGender());
        info.put("invit_cd", member.getInvitCd());
        info.put("mobile_num", member.getMobileNum());
        info.put("region_cd", timezone.getRegionCd());
        info.put("mobile_tz", timezone.getMobileTz());
        info.put("ext_key", member.getExtKey());
        info.put("wallet_addr", walletAddr);
        info.put("cdt", member.getCdt().toString());

        response.put("info", info);
        response.put("rwds", rewardBalance.getSumAmt());
        response.put("token_amt", tokenAmt.toPlainString());
        response.put("bnb_amt", bnbAmt.toPlainString());

        this.log.info("로그인 성공: mIdx={}, nickname={}", mIdx.toString(), member.getNickname());

        return response;
    }
}
