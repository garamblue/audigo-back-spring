package com.audigo.audigo_back.service.admin;

import com.audigo.audigo_back.entity.admin.AdminsLogHisEntity;
import com.audigo.audigo_back.repository.admin.AdminsLogHisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 활동 로그 서비스
 * - API 호출 로그 기록
 * - 로그 조회 (관리자별, 메뉴별, 날짜별)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminLogService {

    private final AdminsLogHisRepository logHisRepository;

    /**
     * 활동 로그 기록
     *
     * @param aIdx         관리자 ID
     * @param menuCd       메뉴 코드
     * @param actionType   액션 타입 (VIEW, CREATE, UPDATE, DELETE)
     * @param apiUrl       API URL
     * @param httpMethod   HTTP 메서드
     * @param requestParams 요청 파라미터
     * @param ipAddr       IP 주소
     * @param userAgent    User-Agent
     * @param resultCode   결과 코드 (SUCCESS, FAIL)
     * @param errorMsg     에러 메시지
     */
    @Transactional
    public void logActivity(BigInteger aIdx, String menuCd, String actionType,
                             String apiUrl, String httpMethod, String requestParams,
                             String ipAddr, String userAgent,
                             String resultCode, String errorMsg) {
        try {
            AdminsLogHisEntity log = new AdminsLogHisEntity();
            log.setAIdx(aIdx);
            log.setMenuCd(menuCd);
            log.setActionType(actionType);
            log.setApiUrl(apiUrl);
            log.setHttpMethod(httpMethod);
            log.setRequestParams(requestParams);
            log.setIpAddr(ipAddr);
            log.setUserAgent(userAgent);
            log.setResultCode(resultCode);
            log.setErrorMsg(errorMsg);

            logHisRepository.save(log);

        } catch (Exception e) {
            log.error("활동 로그 기록 실패: aIdx={}, apiUrl={}", aIdx, apiUrl, e);
        }
    }

    /**
     * 간편 로그 기록 (성공)
     */
    @Transactional
    public void logSuccess(BigInteger aIdx, String menuCd, String actionType,
                            String apiUrl, String httpMethod, String ipAddr, String userAgent) {
        logActivity(aIdx, menuCd, actionType, apiUrl, httpMethod, null,
                ipAddr, userAgent, "SUCCESS", null);
    }

    /**
     * 간편 로그 기록 (실패)
     */
    @Transactional
    public void logFailure(BigInteger aIdx, String menuCd, String actionType,
                            String apiUrl, String httpMethod, String ipAddr,
                            String userAgent, String errorMsg) {
        logActivity(aIdx, menuCd, actionType, apiUrl, httpMethod, null,
                ipAddr, userAgent, "FAIL", errorMsg);
    }

    /**
     * 관리자별 로그 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLogsByAdmin(BigInteger aIdx, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "cdt"));
        Page<AdminsLogHisEntity> logPage = logHisRepository.findByAIdx(aIdx, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("logs", logPage.getContent().stream().map(this::convertToMap).toList());
        result.put("currentPage", logPage.getNumber());
        result.put("totalPages", logPage.getTotalPages());
        result.put("totalElements", logPage.getTotalElements());

        return result;
    }

    /**
     * 메뉴별 로그 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogsByMenu(String menuCd) {
        List<AdminsLogHisEntity> logs = logHisRepository.findByMenuCdOrderByCdtDesc(menuCd);

        return logs.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 액션 타입별 로그 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogsByActionType(String actionType) {
        List<AdminsLogHisEntity> logs = logHisRepository.findByActionTypeOrderByCdtDesc(actionType);

        return logs.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 날짜 범위로 로그 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogsByDateRange(LocalDateTime startDt, LocalDateTime endDt) {
        List<AdminsLogHisEntity> logs = logHisRepository.findByDateRange(startDt, endDt);

        return logs.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 관리자 + 날짜 범위로 로그 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLogsByAdminAndDateRange(
            BigInteger aIdx, LocalDateTime startDt, LocalDateTime endDt) {
        List<AdminsLogHisEntity> logs =
                logHisRepository.findByAIdxAndDateRange(aIdx, startDt, endDt);

        return logs.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 실패 로그 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFailedLogs() {
        List<AdminsLogHisEntity> logs = logHisRepository.findByResultCodeOrderByCdtDesc("FAIL");

        return logs.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 최근 로그 조회 (제한된 개수)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentLogs(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "cdt"));
        Page<AdminsLogHisEntity> logPage = logHisRepository.findAll(pageable);

        return logPage.getContent().stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * Entity를 Map으로 변환
     */
    private Map<String, Object> convertToMap(AdminsLogHisEntity log) {
        Map<String, Object> map = new HashMap<>();
        map.put("alhIdx", log.getAlhIdx().toString());
        map.put("aIdx", log.getAIdx().toString());
        map.put("menuCd", log.getMenuCd());
        map.put("actionType", log.getActionType());
        map.put("apiUrl", log.getApiUrl());
        map.put("httpMethod", log.getHttpMethod());
        map.put("requestParams", log.getRequestParams());
        map.put("ipAddr", log.getIpAddr());
        map.put("userAgent", log.getUserAgent());
        map.put("resultCode", log.getResultCode());
        map.put("errorMsg", log.getErrorMsg());
        map.put("cdt", log.getCdt());
        return map;
    }
}
