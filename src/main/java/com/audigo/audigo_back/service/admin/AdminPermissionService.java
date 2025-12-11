package com.audigo.audigo_back.service.admin;

import com.audigo.audigo_back.entity.admin.AdminsEntity;
import com.audigo.audigo_back.entity.admin.AdminsRolesMenusCustomEntity;
import com.audigo.audigo_back.entity.admin.AdminsRolesMenusEntity;
import com.audigo.audigo_back.repository.admin.AdminsRepository;
import com.audigo.audigo_back.repository.admin.AdminsRolesMenusCustomRepository;
import com.audigo.audigo_back.repository.admin.AdminsRolesMenusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * 관리자 권한 확인 서비스
 * - 역할 기반 권한 확인
 * - 커스텀 권한 확인
 * - 메뉴 접근 권한 확인
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPermissionService {

    private final AdminsRepository adminsRepository;
    private final AdminsRolesMenusRepository rolesMenusRepository;
    private final AdminsRolesMenusCustomRepository rolesMenusCustomRepository;

    /**
     * 관리자의 특정 메뉴에 대한 권한 확인
     *
     * @param aIdx           관리자 ID
     * @param menuCd         메뉴 코드
     * @param permissionType 권한 타입 (detail, post, update, delete, fn)
     * @return 권한 여부
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(BigInteger aIdx, String menuCd, String permissionType) {
        try {
            // 1. 관리자 조회
            AdminsEntity admin = adminsRepository.findById(aIdx)
                    .orElseThrow(() -> new IllegalStateException("관리자 정보를 찾을 수 없습니다."));

            // 활성 상태 확인
            if (!"Y".equals(admin.getActYn())) {
                return false;
            }

            // 2. 커스텀 권한 확인 (우선순위 높음)
            Optional<AdminsRolesMenusCustomEntity> customPermission =
                    rolesMenusCustomRepository.findByAIdxAndMenuCd(aIdx, menuCd);

            if (customPermission.isPresent()) {
                return checkPermissionFlag(customPermission.get(), permissionType);
            }

            // 3. 역할 권한 확인
            Optional<AdminsRolesMenusEntity> rolePermission =
                    rolesMenusRepository.findByRoleCdAndMenuCd(admin.getRoleCd(), menuCd);

            return rolePermission.map(permission -> checkPermissionFlag(permission, permissionType))
                    .orElse(false);

        } catch (Exception e) {
            log.error("권한 확인 오류: aIdx={}, menuCd={}, permissionType={}",
                    aIdx, menuCd, permissionType, e);
            return false;
        }
    }

    /**
     * 관리자의 모든 메뉴 권한 조회
     *
     * @param aIdx 관리자 ID
     * @return 메뉴별 권한 맵
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, Boolean>> getAllPermissions(BigInteger aIdx) {
        try {
            // 1. 관리자 조회
            AdminsEntity admin = adminsRepository.findById(aIdx)
                    .orElseThrow(() -> new IllegalStateException("관리자 정보를 찾을 수 없습니다."));

            // 2. 역할 권한 조회
            List<AdminsRolesMenusEntity> rolePermissions =
                    rolesMenusRepository.findByRoleCd(admin.getRoleCd());

            Map<String, Map<String, Boolean>> result = new HashMap<>();

            // 3. 역할 권한 매핑
            for (AdminsRolesMenusEntity rolePermission : rolePermissions) {
                String menuCd = rolePermission.getMenuCd();
                Map<String, Boolean> permissions = new HashMap<>();
                permissions.put("detail", "Y".equals(rolePermission.getDetail()));
                permissions.put("post", "Y".equals(rolePermission.getPost()));
                permissions.put("update", "Y".equals(rolePermission.getUpdate()));
                permissions.put("delete", "Y".equals(rolePermission.getDelete()));
                permissions.put("fn", "Y".equals(rolePermission.getFn()));
                result.put(menuCd, permissions);
            }

            // 4. 커스텀 권한으로 오버라이드
            List<AdminsRolesMenusCustomEntity> customPermissions =
                    rolesMenusCustomRepository.findByAIdx(aIdx);

            for (AdminsRolesMenusCustomEntity customPermission : customPermissions) {
                String menuCd = customPermission.getMenuCd();
                Map<String, Boolean> permissions = new HashMap<>();
                permissions.put("detail", "Y".equals(customPermission.getDetail()));
                permissions.put("post", "Y".equals(customPermission.getPost()));
                permissions.put("update", "Y".equals(customPermission.getUpdate()));
                permissions.put("delete", "Y".equals(customPermission.getDelete()));
                permissions.put("fn", "Y".equals(customPermission.getFn()));
                result.put(menuCd, permissions); // 덮어쓰기
            }

            return result;

        } catch (Exception e) {
            log.error("전체 권한 조회 오류: aIdx={}", aIdx, e);
            return new HashMap<>();
        }
    }

    /**
     * 관리자의 특정 메뉴 권한 조회
     *
     * @param aIdx   관리자 ID
     * @param menuCd 메뉴 코드
     * @return 권한 맵
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> getMenuPermissions(BigInteger aIdx, String menuCd) {
        try {
            // 1. 관리자 조회
            AdminsEntity admin = adminsRepository.findById(aIdx)
                    .orElseThrow(() -> new IllegalStateException("관리자 정보를 찾을 수 없습니다."));

            // 2. 커스텀 권한 우선 확인
            Optional<AdminsRolesMenusCustomEntity> customPermission =
                    rolesMenusCustomRepository.findByAIdxAndMenuCd(aIdx, menuCd);

            if (customPermission.isPresent()) {
                AdminsRolesMenusCustomEntity perm = customPermission.get();
                Map<String, Boolean> permissions = new HashMap<>();
                permissions.put("detail", "Y".equals(perm.getDetail()));
                permissions.put("post", "Y".equals(perm.getPost()));
                permissions.put("update", "Y".equals(perm.getUpdate()));
                permissions.put("delete", "Y".equals(perm.getDelete()));
                permissions.put("fn", "Y".equals(perm.getFn()));
                return permissions;
            }

            // 3. 역할 권한 확인
            Optional<AdminsRolesMenusEntity> rolePermission =
                    rolesMenusRepository.findByRoleCdAndMenuCd(admin.getRoleCd(), menuCd);

            if (rolePermission.isPresent()) {
                AdminsRolesMenusEntity perm = rolePermission.get();
                Map<String, Boolean> permissions = new HashMap<>();
                permissions.put("detail", "Y".equals(perm.getDetail()));
                permissions.put("post", "Y".equals(perm.getPost()));
                permissions.put("update", "Y".equals(perm.getUpdate()));
                permissions.put("delete", "Y".equals(perm.getDelete()));
                permissions.put("fn", "Y".equals(perm.getFn()));
                return permissions;
            }

            // 권한 없음
            Map<String, Boolean> permissions = new HashMap<>();
            permissions.put("detail", false);
            permissions.put("post", false);
            permissions.put("update", false);
            permissions.put("delete", false);
            permissions.put("fn", false);
            return permissions;

        } catch (Exception e) {
            log.error("메뉴 권한 조회 오류: aIdx={}, menuCd={}", aIdx, menuCd, e);
            return new HashMap<>();
        }
    }

    /**
     * 관리자가 SUPER_ADMIN 역할인지 확인
     */
    @Transactional(readOnly = true)
    public boolean isSuperAdmin(BigInteger aIdx) {
        return adminsRepository.findById(aIdx)
                .map(admin -> "SUPER_ADMIN".equals(admin.getRoleCd()))
                .orElse(false);
    }

    /**
     * 권한 플래그 확인 (역할 권한)
     */
    private boolean checkPermissionFlag(AdminsRolesMenusEntity permission, String permissionType) {
        return switch (permissionType.toLowerCase()) {
            case "detail" -> "Y".equals(permission.getDetail());
            case "post" -> "Y".equals(permission.getPost());
            case "update" -> "Y".equals(permission.getUpdate());
            case "delete" -> "Y".equals(permission.getDelete());
            case "fn" -> "Y".equals(permission.getFn());
            default -> false;
        };
    }

    /**
     * 권한 플래그 확인 (커스텀 권한)
     */
    private boolean checkPermissionFlag(AdminsRolesMenusCustomEntity permission, String permissionType) {
        return switch (permissionType.toLowerCase()) {
            case "detail" -> "Y".equals(permission.getDetail());
            case "post" -> "Y".equals(permission.getPost());
            case "update" -> "Y".equals(permission.getUpdate());
            case "delete" -> "Y".equals(permission.getDelete());
            case "fn" -> "Y".equals(permission.getFn());
            default -> false;
        };
    }
}
