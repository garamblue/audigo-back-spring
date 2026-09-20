package com.audigo.audigo_back.repository.gamification;

import com.audigo.audigo_back.entity.gamification.HoroscopeHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 운세 조회 히스토리 Repository
 */
@Repository
public interface HoroscopeHisRepository extends JpaRepository<HoroscopeHisEntity, Long> {

    /**
     * 특정 회원의 운세 조회 이력
     */
    @Query("SELECT h FROM HoroscopeHisEntity h WHERE h.mIdx = :mIdx ORDER BY h.cdt DESC")
    List<HoroscopeHisEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 특정 시간 범위 내 회원의 운세 조회 수 카운트
     */
    @Query("SELECT COUNT(h) FROM HoroscopeHisEntity h WHERE h.mIdx = :mIdx AND h.cdt >= :startTime AND h.cdt < :endTime")
    Long countByMIdxAndDateRange(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 당일 운세 조회 확인
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM fun.horoscope_his WHERE m_idx = :mIdx AND cdt AT TIME ZONE 'UTC' >= (current_date - interval ':timezoneOffset hour') AT TIME ZONE 'UTC' AND cdt AT TIME ZONE 'UTC' < (current_date + interval '1 day' - interval ':timezoneOffset hour') AT TIME ZONE 'UTC'", nativeQuery = true)
    boolean existsTodayByMIdxAndTimezone(@Param("mIdx") BigInteger mIdx, @Param("timezoneOffset") int timezoneOffset);
}
