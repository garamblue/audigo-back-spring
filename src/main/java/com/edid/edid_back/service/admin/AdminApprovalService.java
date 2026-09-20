package com.edid.edid_back.service.admin;

import com.edid.edid_back.entity.admin.AdminsDetectedDeviceEntity;
import com.edid.edid_back.repository.admin.AdminsDetectedDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 기기/IP 승인 요청 처리 서비스
 * - 승인 대기 목록 조회
 * - 승인/거부 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminApprovalService {

    private final AdminsDetectedDeviceRepository detectedDeviceRepository;

    /**
     * 모든 승인 대기 기기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingApprovals() {
        List<AdminsDetectedDeviceEntity> pendingDevices =
                detectedDeviceRepository.findByApprovedYnOrderByCdtDesc("N");

        return pendingDevices.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 특정 관리자의 승인 대기 기기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingApprovalsByAdmin(BigInteger aIdx) {
        List<AdminsDetectedDeviceEntity> pendingDevices =
                detectedDeviceRepository.findPendingDevicesByAIdx(aIdx);

        return pendingDevices.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 관리자의 승인된 기기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getApprovedDevices(BigInteger aIdx) {
        List<AdminsDetectedDeviceEntity> approvedDevices =
                detectedDeviceRepository.findApprovedDevicesByAIdx(aIdx);

        return approvedDevices.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 기기 승인 처리
     */
    @Transactional
    public void approveDevice(BigInteger addIdx, BigInteger approvedByAIdx) {
        AdminsDetectedDeviceEntity device = detectedDeviceRepository.findById(addIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기기 정보입니다."));

        if ("Y".equals(device.getApprovedYn())) {
            throw new IllegalStateException("이미 승인된 기기입니다.");
        }

        device.setApprovedYn("Y");
        device.setApprovedDt(LocalDateTime.now());
        device.setApprovedBy(approvedByAIdx);

        detectedDeviceRepository.save(device);
        log.info("기기 승인 성공: addIdx={}, aIdx={}, approvedBy={}",
                addIdx, device.getAIdx(), approvedByAIdx);
    }

    /**
     * 기기 승인 거부 (삭제)
     */
    @Transactional
    public void rejectDevice(BigInteger addIdx) {
        AdminsDetectedDeviceEntity device = detectedDeviceRepository.findById(addIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기기 정보입니다."));

        detectedDeviceRepository.delete(device);
        log.info("기기 승인 거부: addIdx={}, aIdx={}", addIdx, device.getAIdx());
    }

    /**
     * 승인된 기기 해제
     */
    @Transactional
    public void revokeDeviceApproval(BigInteger addIdx) {
        AdminsDetectedDeviceEntity device = detectedDeviceRepository.findById(addIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기기 정보입니다."));

        if (!"Y".equals(device.getApprovedYn())) {
            throw new IllegalStateException("승인되지 않은 기기입니다.");
        }

        device.setApprovedYn("N");
        device.setApprovedDt(null);
        device.setApprovedBy(null);

        detectedDeviceRepository.save(device);
        log.info("기기 승인 해제: addIdx={}, aIdx={}", addIdx, device.getAIdx());
    }

    /**
     * 관리자의 모든 기기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllDevicesByAdmin(BigInteger aIdx) {
        List<AdminsDetectedDeviceEntity> devices = detectedDeviceRepository.findByAIdx(aIdx);

        return devices.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * Entity를 Map으로 변환
     */
    private Map<String, Object> convertToMap(AdminsDetectedDeviceEntity device) {
        Map<String, Object> map = new HashMap<>();
        map.put("addIdx", device.getAddIdx().toString());
        map.put("aIdx", device.getAIdx().toString());
        map.put("deviceId", device.getDeviceId());
        map.put("ipAddr", device.getIpAddr());
        map.put("userAgent", device.getUserAgent());
        map.put("approvedYn", device.getApprovedYn());
        map.put("approvedDt", device.getApprovedDt());
        map.put("approvedBy", device.getApprovedBy() != null ?
                device.getApprovedBy().toString() : null);
        map.put("cdt", device.getCdt());
        map.put("udt", device.getUdt());
        return map;
    }
}
