package com.audigo.audigo_back.repository.reward;

import com.audigo.audigo_back.entity.reward.RewardHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardHistoryRepository extends JpaRepository<RewardHistoryEntity, BigInteger> {

    /**
     * Find by reward topup ID
     */
    Optional<RewardHistoryEntity> findByRtIdx(BigInteger rtIdx);

    /**
     * Find by source table reference
     */
    List<RewardHistoryEntity> findByTableIdxAndTableNm(BigInteger tableIdx, String tableNm);

    /**
     * Check if history exists for topup
     */
    boolean existsByRtIdx(BigInteger rtIdx);
}
