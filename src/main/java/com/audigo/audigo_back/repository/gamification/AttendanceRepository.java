package com.audigo.audigo_back.repository.gamification;

import com.audigo.audigo_back.entity.gamification.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 출석 체크 Repository
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    /**
     * 특정 회원의 출석 이력 조회
     */
    @Query("SELECT a FROM AttendanceEntity a WHERE a.mIdx = :mIdx ORDER BY a.cdt DESC")
    List<AttendanceEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 특정 시간 범위 내 회원의 출석 수 카운트
     */
    @Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.mIdx = :mIdx AND a.cdt >= :startTime AND a.cdt < :endTime")
    Long countByMIdxAndDateRange(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 당일 출석 확인
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM fun.daily_attendance_check WHERE m_idx = :mIdx AND cdt::date = CURRENT_DATE", nativeQuery = true)
    boolean existsTodayByMIdx(@Param("mIdx") BigInteger mIdx);
}
