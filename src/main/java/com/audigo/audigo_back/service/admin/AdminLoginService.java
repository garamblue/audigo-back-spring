package com.audigo.audigo_back.service.admin;

import com.audigo.audigo_back.entity.admin.*;
import com.audigo.audigo_back.repository.admin.*;
import com.audigo.audigo_back.util.AesUtil;
import com.audigo.audigo_back.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 관리자 로그인/인증 서비스
 * - 로그인/로그아웃
 * - JWT 토큰 생성/검증
 * - 기기/IP 승인 확인
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminLoginService {

    private final AdminsRepository adminsRepository;
    private final AdminsDetectedDeviceRepository detectedDeviceRepository;
    private final AdminsSessionHisRepository sessionHisRepository;
    private final AdminsLogHisRepository logHisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AesUtil aesUtil;

    /**
     * 관리자 로그인
     *
     * @param encryptedId 암호화된 로그인 ID
     * @param encryptedPw 암호화된 비밀번호
     * @param deviceId    기기 ID
     * @param ipAddr      IP 주소
     * @param userAgent   User-Agent
     * @return 로그인 결과 (JWT 토큰, 기기 승인 상태 등)
     */
    @Transactional
    public Map<String, Object> signIn(String encryptedId, String encryptedPw,
                                       String deviceId, String ipAddr, String userAgent) {
        try {
            // 1. ID 복호화
            String loginId = aesUtil.decryptAdmin(encryptedId, String.class);

            // 2. 관리자 조회
            AdminsEntity admin = adminsRepository.findByIdAndActive(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

            // 3. 비밀번호 확인
            String password = aesUtil.decryptAdmin(encryptedPw, String.class);
            if (!passwordEncoder.matches(password, admin.getPw())) {
                // 로그 기록
                logLoginFailure(admin.getAIdx(), "INVALID_PASSWORD", ipAddr, userAgent);
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            // 4. 기기/IP 승인 확인
            boolean isApproved = detectedDeviceRepository.isDeviceApproved(
                    admin.getAIdx(), deviceId, ipAddr);

            if (!isApproved) {
                // 미승인 기기 등록
                AdminsDetectedDeviceEntity device = detectedDeviceRepository
                        .findByAIdxAndDeviceIdAndIpAddr(admin.getAIdx(), deviceId, ipAddr)
                        .orElseGet(() -> {
                            AdminsDetectedDeviceEntity newDevice = new AdminsDetectedDeviceEntity();
                            newDevice.setAIdx(admin.getAIdx());
                            newDevice.setDeviceId(deviceId);
                            newDevice.setIpAddr(ipAddr);
                            newDevice.setUserAgent(userAgent);
                            newDevice.setApprovedYn("N");
                            return detectedDeviceRepository.save(newDevice);
                        });

                Map<String, Object> response = new HashMap<>();
                response.put("code", "0");
                response.put("msg", "승인되지 않은 기기입니다. 관리자에게 승인을 요청하세요.");
                response.put("requireApproval", true);
                response.put("deviceId", device.getAddIdx());
                return response;
            }

            // 5. JWT 토큰 생성
            Map<String, Object> payload = new HashMap<>();
            payload.put("aIdx", admin.getAIdx().toString());
            payload.put("id", admin.getId());
            payload.put("nm", admin.getNm());
            payload.put("roleCd", admin.getRoleCd());

            String token = jwtUtil.generateAdminToken(payload);

            // 6. 세션 이력 저장
            AdminsSessionHisEntity session = new AdminsSessionHisEntity();
            session.setAIdx(admin.getAIdx());
            session.setSessionToken(token);
            session.setDeviceId(deviceId);
            session.setIpAddr(ipAddr);
            session.setUserAgent(userAgent);
            session.setLoginDt(LocalDateTime.now());
            session.setStts("A");
            sessionHisRepository.save(session);

            // 7. 로그인 성공 로그 기록
            logLoginSuccess(admin.getAIdx(), ipAddr, userAgent);

            // 8. 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("code", "1");
            response.put("msg", "로그인 성공");
            response.put("token", token);
            response.put("aIdx", admin.getAIdx().toString());
            response.put("nm", admin.getNm());
            response.put("roleCd", admin.getRoleCd());

            return response;

        } catch (IllegalArgumentException e) {
            log.error("로그인 실패: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", "0");
            response.put("msg", e.getMessage());
            return response;

        } catch (Exception e) {
            log.error("로그인 오류", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", "0");
            response.put("msg", "로그인 중 오류가 발생했습니다.");
            return response;
        }
    }

    /**
     * 관리자 로그아웃
     */
    @Transactional
    public void signOut(String token, BigInteger aIdx) {
        try {
            // 1. 세션 조회
            AdminsSessionHisEntity session = sessionHisRepository.findBySessionToken(token)
                    .orElse(null);

            if (session != null && session.getAIdx().equals(aIdx)) {
                // 2. 세션 상태 변경
                session.setStts("L"); // Logout
                session.setLogoutDt(LocalDateTime.now());
                sessionHisRepository.save(session);

                // 3. 로그아웃 로그 기록
                logLogout(aIdx, session.getIpAddr(), session.getUserAgent());
            }

        } catch (Exception e) {
            log.error("로그아웃 오류", e);
        }
    }

    /**
     * 토큰 검증 및 관리자 정보 조회
     */
    @Transactional(readOnly = true)
    public AdminsEntity verifyToken(String token) {
        try {
            // 1. JWT 검증
            var claims = jwtUtil.verifyAdminToken(token);
            if (claims == null) {
                return null;
            }

            // 2. aIdx 추출
            String aIdxStr = jwtUtil.getClaimValue(claims, "aIdx");
            if (aIdxStr == null) {
                return null;
            }

            BigInteger aIdx = new BigInteger(aIdxStr);

            // 3. 세션 확인
            AdminsSessionHisEntity session = sessionHisRepository
                    .findBySessionTokenAndStts(token, "A")
                    .orElse(null);

            if (session == null || !session.getAIdx().equals(aIdx)) {
                return null;
            }

            // 4. 관리자 조회
            return adminsRepository.findById(aIdx)
                    .filter(admin -> "Y".equals(admin.getActYn()))
                    .orElse(null);

        } catch (Exception e) {
            log.error("토큰 검증 오류", e);
            return null;
        }
    }

    /**
     * 로그인 성공 로그 기록
     */
    private void logLoginSuccess(BigInteger aIdx, String ipAddr, String userAgent) {
        try {
            AdminsLogHisEntity log = new AdminsLogHisEntity();
            log.setAIdx(aIdx);
            log.setActionType("LOGIN");
            log.setApiUrl("/api/admin/auth/signin");
            log.setHttpMethod("POST");
            log.setIpAddr(ipAddr);
            log.setUserAgent(userAgent);
            log.setResultCode("SUCCESS");
            logHisRepository.save(log);
        } catch (Exception e) {
            log.error("로그 기록 실패", e);
        }
    }

    /**
     * 로그인 실패 로그 기록
     */
    private void logLoginFailure(BigInteger aIdx, String reason, String ipAddr, String userAgent) {
        try {
            AdminsLogHisEntity log = new AdminsLogHisEntity();
            log.setAIdx(aIdx);
            log.setActionType("LOGIN");
            log.setApiUrl("/api/admin/auth/signin");
            log.setHttpMethod("POST");
            log.setIpAddr(ipAddr);
            log.setUserAgent(userAgent);
            log.setResultCode("FAIL");
            log.setErrorMsg(reason);
            logHisRepository.save(log);
        } catch (Exception e) {
            log.error("로그 기록 실패", e);
        }
    }

    /**
     * 로그아웃 로그 기록
     */
    private void logLogout(BigInteger aIdx, String ipAddr, String userAgent) {
        try {
            AdminsLogHisEntity log = new AdminsLogHisEntity();
            log.setAIdx(aIdx);
            log.setActionType("LOGOUT");
            log.setApiUrl("/api/admin/auth/signout");
            log.setHttpMethod("POST");
            log.setIpAddr(ipAddr);
            log.setUserAgent(userAgent);
            log.setResultCode("SUCCESS");
            logHisRepository.save(log);
        } catch (Exception e) {
            log.error("로그 기록 실패", e);
        }
    }

    /**
     * 만료된 세션 정리
     */
    @Transactional
    public void cleanupExpiredSessions() {
        try {
            LocalDateTime expireDt = LocalDateTime.now().minusDays(1);
            List<AdminsSessionHisEntity> expiredSessions =
                    sessionHisRepository.findExpiredSessions(expireDt);

            for (AdminsSessionHisEntity session : expiredSessions) {
                session.setStts("E"); // Expired
                sessionHisRepository.save(session);
            }

            log.info("만료된 세션 정리 완료: {} 건", expiredSessions.size());

        } catch (Exception e) {
            log.error("세션 정리 오류", e);
        }
    }
}
