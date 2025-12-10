package com.audigo.audigo_back.repository.advertisement;

import com.audigo.audigo_back.entity.advertisement.AdsSendHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 광고 배포 히스토리 Repository
 */
@Repository
public interface AdsSendHisRepository extends JpaRepository<AdsSendHisEntity, Long> {

    /**
     * 특정 회원에게 배포된 광고 이력 조회
     */
    @Query("SELECT a FROM AdsSendHisEntity a WHERE a.mIdx = :mIdx ORDER BY a.cdt DESC")
    List<AdsSendHisEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 특정 광고의 배포 이력 조회
     */
    @Query("SELECT a FROM AdsSendHisEntity a WHERE a.aaIdx = :aaIdx ORDER BY a.cdt DESC")
    List<AdsSendHisEntity> findByAaIdx(@Param("aaIdx") Long aaIdx);

    /**
     * 특정 회원의 특정 광고 배포 이력 조회
     */
    @Query("SELECT a FROM AdsSendHisEntity a WHERE a.mIdx = :mIdx AND a.aaIdx = :aaIdx")
    Optional<AdsSendHisEntity> findByMIdxAndAaIdx(@Param("mIdx") BigInteger mIdx, @Param("aaIdx") Long aaIdx);

    /**
     * 특정 시간 범위 내 회원의 광고 배포 수 카운트 (일일 한도 체크용)
     */
    @Query("SELECT COUNT(a) FROM AdsSendHisEntity a WHERE a.mIdx = :mIdx AND a.cdt >= :startTime AND a.cdt < :endTime")
    Long countByMIdxAndDateRange(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 특정 회원의 당일 배포된 광고 ID 목록 조회 (중복 방지용)
     */
    @Query("SELECT a.aaIdx FROM AdsSendHisEntity a WHERE a.mIdx = :mIdx AND a.cdt >= :startTime AND a.cdt < :endTime")
    List<Long> findTodayAdIdsByMIdx(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
