package com.edid.edid_back.service.admin;

import com.edid.edid_back.entity.admin.AdminsRolesEntity;
import com.edid.edid_back.entity.admin.AdminsRolesMenusEntity;
import com.edid.edid_back.repository.admin.AdminsRolesRepository;
import com.edid.edid_back.repository.admin.AdminsRolesMenusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 역할 관리 서비스
 * - 역할 CRUD
 * - 역할별 메뉴 권한 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRoleService {

    private final AdminsRolesRepository rolesRepository;
    private final AdminsRolesMenusRepository rolesMenusRepository;

    /**
     * 역할 등록
     */
    @Transactional
    public void createRole(String roleCd, String roleNm) {
        // 역할 코드 중복 확인
        if (rolesRepository.existsByRoleCd(roleCd)) {
            throw new IllegalArgumentException("이미 존재하는 역할 코드입니다.");
        }

        AdminsRolesEntity role = new AdminsRolesEntity();
        role.setRoleCd(roleCd);
        role.setRoleNm(roleNm);
        role.setStts("1");

        rolesRepository.save(role);
        log.info("역할 등록 성공: roleCd={}, roleNm={}", roleCd, roleNm);
    }

    /**
     * 역할 수정
     */
    @Transactional
    public void updateRole(String roleCd, String roleNm) {
        AdminsRolesEntity role = rolesRepository.findByRoleCd(roleCd)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));

        if (roleNm != null) {
            role.setRoleNm(roleNm);
        }

        rolesRepository.save(role);
        log.info("역할 수정 성공: roleCd={}", roleCd);
    }

    /**
     * 역할 삭제 (비활성화)
     */
    @Transactional
    public void deleteRole(String roleCd) {
        AdminsRolesEntity role = rolesRepository.findByRoleCd(roleCd)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));

        role.setStts("0");
        rolesRepository.save(role);
        log.info("역할 삭제 성공: roleCd={}", roleCd);
    }

    /**
     * 활성 역할 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveRoles() {
        List<AdminsRolesEntity> roles = rolesRepository.findByStts("1");

        return roles.stream()
                .map(this::convertToMap)
                .toList();
    }

    /**
     * 역할별 메뉴 권한 설정
     *
     * @param roleCd         역할 코드
     * @param menuCd         메뉴 코드
     * @param detail         조회 권한
     * @param post           등록 권한
     * @param update         수정 권한
     * @param delete         삭제 권한
     * @param fn             특수 기능 권한
     */
    @Transactional
    public void setRoleMenuPermission(String roleCd, String menuCd,
                                       boolean detail, boolean post, boolean update,
                                       boolean delete, boolean fn) {
        // 기존 권한 확인
        AdminsRolesMenusEntity permission = rolesMenusRepository
                .findByRoleCdAndMenuCd(roleCd, menuCd)
                .orElseGet(() -> {
                    AdminsRolesMenusEntity newPermission = new AdminsRolesMenusEntity();
                    newPermission.setRoleCd(roleCd);
                    newPermission.setMenuCd(menuCd);
                    return newPermission;
                });

        permission.setDetail(detail ? "Y" : "N");
        permission.setPost(post ? "Y" : "N");
        permission.setUpdate(update ? "Y" : "N");
        permission.setDelete(delete ? "Y" : "N");
        permission.setFn(fn ? "Y" : "N");

        rolesMenusRepository.save(permission);
        log.info("역할 메뉴 권한 설정 성공: roleCd={}, menuCd={}", roleCd, menuCd);
    }

    /**
     * 역할의 메뉴 권한 삭제
     */
    @Transactional
    public void deleteRoleMenuPermission(String roleCd, String menuCd) {
        rolesMenusRepository.findByRoleCdAndMenuCd(roleCd, menuCd)
                .ifPresent(permission -> {
                    rolesMenusRepository.delete(permission);
                    log.info("역할 메뉴 권한 삭제 성공: roleCd={}, menuCd={}", roleCd, menuCd);
                });
    }

    /**
     * 역할의 모든 메뉴 권한 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRoleMenuPermissions(String roleCd) {
        List<AdminsRolesMenusEntity> permissions = rolesMenusRepository.findByRoleCd(roleCd);

        return permissions.stream()
                .map(this::convertPermissionToMap)
                .toList();
    }

    /**
     * 역할 Entity를 Map으로 변환
     */
    private Map<String, Object> convertToMap(AdminsRolesEntity role) {
        Map<String, Object> map = new HashMap<>();
        map.put("arIdx", role.getArIdx().toString());
        map.put("roleCd", role.getRoleCd());
        map.put("roleNm", role.getRoleNm());
        map.put("stts", role.getStts());
        map.put("cdt", role.getCdt());
        map.put("udt", role.getUdt());
        return map;
    }

    /**
     * 권한 Entity를 Map으로 변환
     */
    private Map<String, Object> convertPermissionToMap(AdminsRolesMenusEntity permission) {
        Map<String, Object> map = new HashMap<>();
        map.put("armIdx", permission.getArmIdx().toString());
        map.put("roleCd", permission.getRoleCd());
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
