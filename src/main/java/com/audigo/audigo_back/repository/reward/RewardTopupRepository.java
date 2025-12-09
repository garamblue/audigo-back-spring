package com.audigo.audigo_back.repository.reward;

import com.audigo.audigo_back.entity.reward.RewardTopupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardTopupRepository extends JpaRepository<RewardTopupEntity, BigInteger> {

    /**
     * Find all topups for a member
     */
    @Query("SELECT rt FROM RewardTopupEntity rt WHERE rt.mIdx = :mIdx ORDER BY rt.tranDt DESC")
    List<RewardTopupEntity> findByMIdxOrderByTranDtDesc(@Param("mIdx") BigInteger mIdx);

    /**
     * Find topups for a member with pagination
     */
    @Query("SELECT rt FROM RewardTopupEntity rt WHERE rt.mIdx = :mIdx ORDER BY rt.tranDt DESC")
    Page<RewardTopupEntity> findByMIdxOrderByTranDtDesc(@Param("mIdx") BigInteger mIdx, Pageable pageable);

    /**
     * Find topups by member and date range
     */
    @Query("SELECT rt FROM RewardTopupEntity rt WHERE rt.mIdx = :mIdx AND rt.tranDt BETWEEN :startDate AND :endDate ORDER BY rt.tranDt DESC")
    List<RewardTopupEntity> findByMIdxAndDateRange(@Param("mIdx") BigInteger mIdx,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find topups by member and reward code
     */
    @Query("SELECT rt FROM RewardTopupEntity rt WHERE rt.mIdx = :mIdx AND rt.cd = :cd")
    List<RewardTopupEntity> findByMIdxAndCd(@Param("mIdx") BigInteger mIdx, @Param("cd") String cd);

    /**
     * Sum total topup amount for member
     */
    @Query("SELECT COALESCE(SUM(rt.rAmt), 0) FROM RewardTopupEntity rt WHERE rt.mIdx = :mIdx")
    BigDecimal sumByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * Sum topup amount by month (for expiration calculation)
     */
    @Query(value = "SELECT COALESCE(SUM(r_amt), 0) FROM rwds.reward_topup " +
                   "WHERE m_idx = :mIdx AND DATE_TRUNC('month', tran_dt) = DATE_TRUNC('month', :month::timestamp)",
           nativeQuery = true)
    BigDecimal sumByMIdxAndMonth(@Param("mIdx") BigInteger mIdx, @Param("month") LocalDateTime month);

    /**
     * Find topups within last N days for specific codes
     */
    @Query("SELECT rt FROM RewardTopupEntity rt WHERE rt.mIdx = :mIdx AND rt.cd IN :codes AND rt.tranDt >= :sinceDate ORDER BY rt.tranDt DESC")
    List<RewardTopupEntity> findRecentByMIdxAndCodes(@Param("mIdx") BigInteger mIdx,
                                                       @Param("codes") List<String> codes,
                                                       @Param("sinceDate") LocalDateTime sinceDate);
}
