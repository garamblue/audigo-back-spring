package com.audigo.audigo_back.service.admin;

import com.audigo.audigo_back.entity.admin.AdminsEntity;
import com.audigo.audigo_back.entity.admin.AdminsRolesMenusCustomEntity;
import com.audigo.audigo_back.repository.admin.AdminsRepository;
import com.audigo.audigo_back.repository.admin.AdminsRolesMenusCustomRepository;
import com.audigo.audigo_back.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 사용자 관리 서비스
 * - 관리자 CRUD
 * - 커스텀 메뉴 권한 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final AdminsRepository adminsRepository;
    private final AdminsRolesMenusCustomRepository customPermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesUtil aesUtil;

    /**
     * 관리자 등록
     */
    @Transactional
    public void createAdmin(String id, String password, String nm, String mobile, String roleCd) {
        // ID 중복 확인
        String encryptedId = aesUtil.encryptAdmin(id);
        if (adminsRepository.existsById(encryptedId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        AdminsEntity admin = new AdminsEntity();
        admin.setId(encryptedId); // ID 암호화 저장
        admin.setPw(passwordEncoder.encode(password)); // 비밀번호 해시
        admin.setNm(nm);
        admin.setMobile(mobile);
        admin.setRoleCd(roleCd);
        admin.setActYn("Y");

        adminsRepository.save(admin);
        log.info("관리자 등록 성공: id={}, nm={}, roleCd={}", id, nm, roleCd);
    }

    /**
     * 관리자 정보 수정
     */
    @Transactional
    public void updateAdmin(BigInteger aIdx, String nm, String mobile, String roleCd) {
        AdminsEntity admin = adminsRepository.findById(aIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        if (nm != null) admin.setNm(nm);
        if (mobile != null) admin.setMobile(mobile);
        if (roleCd != null) admin.setRoleCd(roleCd);

        adminsRepository.save(admin);
        log.info("관리자 수정 성공: aIdx={}", aIdx);
    }

    /**
     * 관리자 비밀번호 변경
     */
    @Transactional
    public void updatePassword(BigInteger aIdx, String newPassword) {
        AdminsEntity admin = adminsRepository.findById(aIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        admin.setPw(passwordEncoder.encode(newPassword));
        adminsRepository.save(admin);
        log.info("관리자 비밀번호 변경 성공: aIdx={}", aIdx);
    }

    /**
     * 관리자 활성화/비활성화
     */
    @Transactional
    public void toggleAdminStatus(BigInteger aIdx) {
        AdminsEntity admin = adminsRepository.findById(aIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        String newStatus = "Y".equals(admin.getActYn()) ? "N" : "Y";
        admin.setActYn(newStatus);
        adminsRepository.save(admin);
        log.info("관리자 상태 변경 성공: aIdx={}, actYn={}", aIdx, newStatus);
    }

    /**
     * 활성 관리자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveAdmins() {
        List<AdminsEntity> admins = adminsRepository.findByActYn("Y");

        return admins.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 모든 관리자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllAdmins() {
        List<AdminsEntity> admins = adminsRepository.findAll();

        return admins.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 관리자 상세 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdmin(BigInteger aIdx) {
        AdminsEntity admin = adminsRepository.findById(aIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        return convertToMap(admin);
    }

    /**
     * 관리자 커스텀 메뉴 권한 설정
     */
    @Transactional
    public void setCustomMenuPermission(BigInteger aIdx, String menuCd,
                                         boolean detail, boolean post, boolean update,
                                         boolean delete, boolean fn) {
        // 기존 커스텀 권한 확인
        AdminsRolesMenusCustomEntity permission = customPermissionRepository
                .findByAIdxAndMenuCd(aIdx, menuCd)
                .orElseGet(() -> {
                    AdminsRolesMenusCustomEntity newPermission = new AdminsRolesMenusCustomEntity();
                    newPermission.setAIdx(aIdx);
                    newPermission.setMenuCd(menuCd);
                    return newPermission;
                });

        permission.setDetail(detail ? "Y" : "N");
        permission.setPost(post ? "Y" : "N");
        permission.setUpdate(update ? "Y" : "N");
        permission.setDelete(delete ? "Y" : "N");
        permission.setFn(fn ? "Y" : "N");

        customPermissionRepository.save(permission);
        log.info("관리자 커스텀 권한 설정 성공: aIdx={}, menuCd={}", aIdx, menuCd);
    }

    /**
     * 관리자 커스텀 권한 삭제
     */
    @Transactional
    public void deleteCustomMenuPermission(BigInteger aIdx, String menuCd) {
        customPermissionRepository.findByAIdxAndMenuCd(aIdx, menuCd)
                .ifPresent(permission -> {
                    customPermissionRepository.delete(permission);
                    log.info("관리자 커스텀 권한 삭제 성공: aIdx={}, menuCd={}", aIdx, menuCd);
                });
    }

    /**
     * 관리자의 모든 커스텀 권한 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCustomMenuPermissions(BigInteger aIdx) {
        List<AdminsRolesMenusCustomEntity> permissions = customPermissionRepository.findByAIdx(aIdx);

        return permissions.stream()
                .map(this::convertCustomPermissionToMap)
                .toList();
    }

    /**
     * Entity를 Map으로 변환 (ID 복호화)
     */
    private Map<String, Object> convertToMap(AdminsEntity admin) {
        Map<String, Object> map = new HashMap<>();
        map.put("aIdx", admin.getAIdx().toString());
        // ID 복호화하여 반환
        try {
            String decryptedId = aesUtil.decryptAdmin(admin.getId(), String.class);
            map.put("id", decryptedId);
        } catch (Exception e) {
            log.error("ID 복호화 실패: aIdx={}", admin.getAIdx(), e);
            map.put("id", "***");
        }
        map.put("nm", admin.getNm());
        map.put("mobile", admin.getMobile());
        map.put("roleCd", admin.getRoleCd());
        map.put("actYn", admin.getActYn());
        map.put("cdt", admin.getCdt());
        map.put("udt", admin.getUdt());
        return map;
    }

    /**
     * 커스텀 권한 Entity를 Map으로 변환
     */
    private Map<String, Object> convertCustomPermissionToMap(AdminsRolesMenusCustomEntity permission) {
        Map<String, Object> map = new HashMap<>();
        map.put("armcIdx", permission.getArmcIdx().toString());
        map.put("aIdx", permission.getAIdx().toString());
        map.put("menuCd", permission.getMenuCd());
        map.put("detail", "Y".equals(permission.getDetail()));
        map.put("post", "Y".equals(permission.getPost()));
        map.put("update", "Y".equals(permission.getUpdate()));
        map.put("delete", "Y".equals(permission.getDelete()));
        map.put("fn", "Y".equals(permission.getFn()));
        map.put("cdt", permission.getCdt());
        map.put("udt", permission.getUdt());
        return map;
    }
}
