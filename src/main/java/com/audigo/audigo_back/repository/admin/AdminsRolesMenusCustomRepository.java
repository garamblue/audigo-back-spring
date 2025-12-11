package com.audigo.audigo_back.repository.admin;

import com.audigo.audigo_back.entity.admin.AdminsRolesMenusCustomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsRolesMenusCustomRepository extends JpaRepository<AdminsRolesMenusCustomEntity, BigInteger> {

    /**
     * 관리자 ID로 커스텀 권한 목록 조회
     */
    List<AdminsRolesMenusCustomEntity> findByAIdx(BigInteger aIdx);

    /**
     * 관리자 ID와 메뉴 코드로 커스텀 권한 조회
     */
    Optional<AdminsRolesMenusCustomEntity> findByAIdxAndMenuCd(BigInteger aIdx, String menuCd);

    /**
     * 관리자의 모든 커스텀 권한 삭제
     */
    void deleteByAIdx(BigInteger aIdx);

    /**
     * 특정 관리자에게 특정 커스텀 권한이 있는지 확인
     */
    @Query("SELECT CASE WHEN " +
           "(armc.detail = 'Y' AND :permissionType = 'detail') OR " +
           "(armc.post = 'Y' AND :permissionType = 'post') OR " +
           "(armc.update = 'Y' AND :permissionType = 'update') OR " +
           "(armc.delete = 'Y' AND :permissionType = 'delete') OR " +
           "(armc.fn = 'Y' AND :permissionType = 'fn') " +
           "THEN true ELSE false END " +
           "FROM AdminsRolesMenusCustomEntity armc " +
           "WHERE armc.aIdx = :aIdx AND armc.menuCd = :menuCd")
    boolean hasCustomPermission(@Param("aIdx") BigInteger aIdx,
                                @Param("menuCd") String menuCd,
                                @Param("permissionType") String permissionType);
}
