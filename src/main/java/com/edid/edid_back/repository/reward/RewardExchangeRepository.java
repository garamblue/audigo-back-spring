package com.audigo.audigo_back.repository.reward;

import com.audigo.audigo_back.entity.reward.RewardExchangeEntity;
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
public interface RewardExchangeRepository extends JpaRepository<RewardExchangeEntity, BigInteger> {

    /**
     * Find all exchanges for a member
     */
    @Query("SELECT re FROM RewardExchangeEntity re WHERE re.mIdx = :mIdx ORDER BY re.tranDt DESC")
    List<RewardExchangeEntity> findByMIdxOrderByTranDtDesc(@Param("mIdx") BigInteger mIdx);

    /**
     * Find exchanges for a member with pagination
     */
    @Query("SELECT re FROM RewardExchangeEntity re WHERE re.mIdx = :mIdx ORDER BY re.tranDt DESC")
    Page<RewardExchangeEntity> findByMIdxOrderByTranDtDesc(@Param("mIdx") BigInteger mIdx, Pageable pageable);

    /**
     * Find exchanges by member and date range
     */
    @Query("SELECT re FROM RewardExchangeEntity re WHERE re.mIdx = :mIdx AND re.tranDt BETWEEN :startDate AND :endDate ORDER BY re.tranDt DESC")
    List<RewardExchangeEntity> findByMIdxAndDateRange(@Param("mIdx") BigInteger mIdx,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Sum total exchange amount for member
     */
    @Query("SELECT COALESCE(SUM(re.rAmt), 0) FROM RewardExchangeEntity re WHERE re.mIdx = :mIdx")
    BigDecimal sumByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * Sum exchange amount after specific date (for expiration calculation)
     */
    @Query("SELECT COALESCE(SUM(re.rAmt), 0) FROM RewardExchangeEntity re WHERE re.mIdx = :mIdx AND re.tranDt > :sinceDate")
    BigDecimal sumByMIdxAfterDate(@Param("mIdx") BigInteger mIdx, @Param("sinceDate") LocalDateTime sinceDate);

    /**
     * Find exchanges by table reference
     */
    List<RewardExchangeEntity> findByTableIdxAndTableNm(BigInteger tableIdx, String tableNm);
}
