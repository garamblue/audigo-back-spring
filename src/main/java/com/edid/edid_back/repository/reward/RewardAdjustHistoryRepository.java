package com.audigo.audigo_back.repository.reward;

import com.audigo.audigo_back.entity.reward.RewardAdjustHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface RewardAdjustHistoryRepository extends JpaRepository<RewardAdjustHistoryEntity, BigInteger> {

    /**
     * Find adjustments by member
     */
    @Query("SELECT rah FROM RewardAdjustHistoryEntity rah WHERE rah.mIdx = :mIdx ORDER BY rah.cdt DESC")
    Page<RewardAdjustHistoryEntity> findByMIdxOrderByCdtDesc(@Param("mIdx") BigInteger mIdx, Pageable pageable);

    /**
     * Find adjustments by type
     */
    List<RewardAdjustHistoryEntity> findByTypeOrderByCdtDesc(String type);

    /**
     * Find scheduled adjustments (tran_dt in future)
     */
    @Query("SELECT rah FROM RewardAdjustHistoryEntity rah WHERE rah.tranDt IS NOT NULL AND rah.tranDt > CURRENT_TIMESTAMP ORDER BY rah.tranDt ASC")
    List<RewardAdjustHistoryEntity> findScheduledAdjustments();

    /**
     * Find adjustments to process (tran_dt matches current hour)
     */
    @Query(value = "SELECT * FROM rwds.reward_adjust_his WHERE DATE_TRUNC('hour', tran_dt) = DATE_TRUNC('hour', NOW())", nativeQuery = true)
    List<RewardAdjustHistoryEntity> findAdjustmentsToProcess();

    /**
     * Find scheduled adjustments for specific member
     */
    @Query("SELECT rah FROM RewardAdjustHistoryEntity rah WHERE rah.mIdx = :mIdx AND rah.tranDt IS NOT NULL AND rah.tranDt > CURRENT_TIMESTAMP ORDER BY rah.tranDt ASC")
    List<RewardAdjustHistoryEntity> findScheduledByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * Find adjustments with filters (for admin search)
     */
    @Query("SELECT rah FROM RewardAdjustHistoryEntity rah WHERE " +
           "(:type IS NULL OR rah.type = :type) AND " +
           "(:mIdx IS NULL OR rah.mIdx = :mIdx) " +
           "ORDER BY rah.cdt DESC")
    Page<RewardAdjustHistoryEntity> findWithFilters(@Param("type") String type,
                                                      @Param("mIdx") BigInteger mIdx,
                                                      Pageable pageable);
}
