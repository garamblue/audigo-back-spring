package com.edid.edid_back.repository.admin;

import com.edid.edid_back.entity.admin.AdminsRolesMenusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsRolesMenusRepository extends JpaRepository<AdminsRolesMenusEntity, BigInteger> {

    /**
     * 역할 코드로 권한 목록 조회
     */
    List<AdminsRolesMenusEntity> findByRoleCd(String roleCd);

    /**
     * 역할 코드와 메뉴 코드로 권한 조회
     */
    Optional<AdminsRolesMenusEntity> findByRoleCdAndMenuCd(String roleCd, String menuCd);

    /**
     * 메뉴 코드로 권한 목록 조회
     */
    List<AdminsRolesMenusEntity> findByMenuCd(String menuCd);

    /**
     * 역할의 모든 메뉴 권한 삭제
     */
    void deleteByRoleCd(String roleCd);

    /**
     * 특정 역할에 특정 권한이 있는지 확인
     */
    @Query("SELECT CASE WHEN " +
           "(arm.detail = 'Y' AND :permissionType = 'detail') OR " +
           "(arm.post = 'Y' AND :permissionType = 'post') OR " +
           "(arm.update = 'Y' AND :permissionType = 'update') OR " +
           "(arm.delete = 'Y' AND :permissionType = 'delete') OR " +
           "(arm.fn = 'Y' AND :permissionType = 'fn') " +
           "THEN true ELSE false END " +
           "FROM AdminsRolesMenusEntity arm " +
           "WHERE arm.roleCd = :roleCd AND arm.menuCd = :menuCd")
    boolean hasPermission(@Param("roleCd") String roleCd,
                          @Param("menuCd") String menuCd,
                          @Param("permissionType") String permissionType);
}
