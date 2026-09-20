package com.edid.edid_back.repository.admin;

import com.edid.edid_back.entity.admin.AdminsLogHisEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminsLogHisRepository extends JpaRepository<AdminsLogHisEntity, BigInteger> {

    /**
     * 관리자 ID로 로그 목록 조회 (최신순)
     */
    @Query("SELECT l FROM AdminsLogHisEntity l WHERE l.aIdx = :aIdx ORDER BY l.cdt DESC")
    List<AdminsLogHisEntity> findByAIdxOrderByCdtDesc(@Param("aIdx") BigInteger aIdx);

    /**
     * 관리자 ID로 로그 페이징 조회
     */
    @Query("SELECT l FROM AdminsLogHisEntity l WHERE l.aIdx = :aIdx")
    Page<AdminsLogHisEntity> findByAIdx(@Param("aIdx") BigInteger aIdx, Pageable pageable);

    /**
     * 메뉴 코드로 로그 목록 조회
     */
    List<AdminsLogHisEntity> findByMenuCdOrderByCdtDesc(String menuCd);

    /**
     * 액션 타입으로 로그 목록 조회
     */
    List<AdminsLogHisEntity> findByActionTypeOrderByCdtDesc(String actionType);

    /**
     * 날짜 범위로 로그 조회
     */
    @Query("SELECT l FROM AdminsLogHisEntity l " +
           "WHERE l.cdt BETWEEN :startDt AND :endDt " +
           "ORDER BY l.cdt DESC")
    List<AdminsLogHisEntity> findByDateRange(@Param("startDt") LocalDateTime startDt,
                                               @Param("endDt") LocalDateTime endDt);

    /**
     * 관리자와 날짜 범위로 로그 조회
     */
    @Query("SELECT l FROM AdminsLogHisEntity l " +
           "WHERE l.aIdx = :aIdx " +
           "AND l.cdt BETWEEN :startDt AND :endDt " +
           "ORDER BY l.cdt DESC")
    List<AdminsLogHisEntity> findByAIdxAndDateRange(@Param("aIdx") BigInteger aIdx,
                                                      @Param("startDt") LocalDateTime startDt,
                                                      @Param("endDt") LocalDateTime endDt);

    /**
     * 실패한 로그 조회
     */
    List<AdminsLogHisEntity> findByResultCodeOrderByCdtDesc(String resultCode);
}
