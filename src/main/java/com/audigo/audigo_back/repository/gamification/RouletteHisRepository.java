package com.audigo.audigo_back.repository.gamification;

import com.audigo.audigo_back.entity.gamification.RouletteHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * 룰렛 사용 히스토리 Repository
 */
@Repository
public interface RouletteHisRepository extends JpaRepository<RouletteHisEntity, Long> {

    /**
     * 특정 회원의 룰렛 사용 이력 조회
     */
    @Query("SELECT r FROM RouletteHisEntity r WHERE r.mIdx = :mIdx ORDER BY r.cdt DESC")
    List<RouletteHisEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 최근 고액 당첨자 목록 (500 이상)
     */
    @Query(value = "SELECT rchu.rcuh_idx, rchu.m_idx, rchu.r_amt, rchu.cdt, m.nickname " +
            "FROM fun.roulette_coupon_usage_his rchu " +
            "LEFT JOIN users.members m ON m.m_idx = rchu.m_idx " +
            "WHERE rchu.r_amt >= :minAmount " +
            "ORDER BY rchu.cdt DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> findRecentWinners(@Param("minAmount") BigDecimal minAmount, @Param("limit") int limit);
}
