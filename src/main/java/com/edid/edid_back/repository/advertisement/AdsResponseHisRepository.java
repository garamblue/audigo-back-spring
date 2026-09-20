package com.audigo.audigo_back.repository.advertisement;

import com.audigo.audigo_back.entity.advertisement.AdsResponseHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 광고 응답 히스토리 Repository
 */
@Repository
public interface AdsResponseHisRepository extends JpaRepository<AdsResponseHisEntity, Long> {

    /**
     * 특정 회원의 응답 이력 조회
     */
    @Query("SELECT a FROM AdsResponseHisEntity a WHERE a.mIdx = :mIdx ORDER BY a.cdt DESC")
    List<AdsResponseHisEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 특정 광고의 응답 이력 조회
     */
    @Query("SELECT a FROM AdsResponseHisEntity a WHERE a.aaIdx = :aaIdx ORDER BY a.cdt DESC")
    List<AdsResponseHisEntity> findByAaIdx(@Param("aaIdx") Long aaIdx);

    /**
     * 특정 배포 이력에 대한 응답 조회
     */
    @Query("SELECT a FROM AdsResponseHisEntity a WHERE a.ashIdx = :ashIdx")
    Optional<AdsResponseHisEntity> findByAshIdx(@Param("ashIdx") Long ashIdx);

    /**
     * 회원의 특정 광고 응답 이력 확인
     */
    @Query("SELECT a FROM AdsResponseHisEntity a WHERE a.mIdx = :mIdx AND a.aaIdx = :aaIdx")
    Optional<AdsResponseHisEntity> findByMIdxAndAaIdx(@Param("mIdx") BigInteger mIdx, @Param("aaIdx") Long aaIdx);

    /**
     * 특정 시간 범위 내 회원의 광고 응답 수 카운트 (일일 한도 체크용)
     */
    @Query("SELECT COUNT(a) FROM AdsResponseHisEntity a WHERE a.mIdx = :mIdx AND a.cdt >= :startTime AND a.cdt < :endTime")
    Long countByMIdxAndDateRange(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 특정 모드의 응답 이력 조회
     */
    @Query("SELECT a FROM AdsResponseHisEntity a WHERE a.mIdx = :mIdx AND a.mode = :mode ORDER BY a.cdt DESC")
    List<AdsResponseHisEntity> findByMIdxAndMode(@Param("mIdx") BigInteger mIdx, @Param("mode") String mode);

    /**
     * 특정 시간 범위 내 회원의 응답한 광고 ID 목록 조회
     */
    @Query("SELECT a.aaIdx FROM AdsResponseHisEntity a WHERE a.mIdx = :mIdx AND a.cdt >= :startTime AND a.cdt < :endTime")
    List<Long> findRespondedAdIdsByMIdx(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 회원의 퀴즈 정답 통계 (정답률 계산용)
     */
    @Query("SELECT COUNT(a) FROM AdsResponseHisEntity a WHERE a.mIdx = :mIdx AND a.answer IS NOT NULL")
    Long countAnsweredByMIdx(@Param("mIdx") BigInteger mIdx);
}
