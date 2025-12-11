package com.audigo.audigo_back.repository.admin;

import com.audigo.audigo_back.entity.admin.AdminsSessionHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsSessionHisRepository extends JpaRepository<AdminsSessionHisEntity, BigInteger> {

    /**
     * 세션 토큰으로 세션 조회
     */
    Optional<AdminsSessionHisEntity> findBySessionToken(String sessionToken);

    /**
     * 관리자 ID로 세션 목록 조회 (최신순)
     */
    List<AdminsSessionHisEntity> findByAIdxOrderByLoginDtDesc(BigInteger aIdx);

    /**
     * 관리자의 활성 세션 조회
     */
    @Query("SELECT s FROM AdminsSessionHisEntity s " +
           "WHERE s.aIdx = :aIdx AND s.stts = 'A' " +
           "ORDER BY s.loginDt DESC")
    List<AdminsSessionHisEntity> findActiveSessionsByAIdx(@Param("aIdx") BigInteger aIdx);

    /**
     * 세션 토큰과 상태로 세션 조회
     */
    Optional<AdminsSessionHisEntity> findBySessionTokenAndStts(String sessionToken, String stts);

    /**
     * 만료된 세션 조회 (로그인 후 24시간 경과)
     */
    @Query("SELECT s FROM AdminsSessionHisEntity s " +
           "WHERE s.stts = 'A' AND s.loginDt < :expireDt")
    List<AdminsSessionHisEntity> findExpiredSessions(@Param("expireDt") LocalDateTime expireDt);
}
