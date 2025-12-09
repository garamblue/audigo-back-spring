package com.audigo.audigo_back.repository.reward;

import com.audigo.audigo_back.entity.reward.RewardBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface RewardBalanceRepository extends JpaRepository<RewardBalanceEntity, BigInteger> {

    /**
     * Find reward balance by member ID
     */
    @Query("SELECT rb FROM RewardBalanceEntity rb WHERE rb.mIdx = :mIdx")
    Optional<RewardBalanceEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * Check if balance exists for member
     */
    @Query("SELECT CASE WHEN COUNT(rb) > 0 THEN true ELSE false END FROM RewardBalanceEntity rb WHERE rb.mIdx = :mIdx")
    boolean existsByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * Find reward balance with pessimistic write lock (for concurrent updates)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rb FROM RewardBalanceEntity rb WHERE rb.mIdx = :mIdx")
    Optional<RewardBalanceEntity> findByMIdxWithLock(@Param("mIdx") BigInteger mIdx);

    /**
     * Add amount to member's reward balance
     */
    @Modifying
    @Query("UPDATE RewardBalanceEntity rb SET rb.sumAmt = rb.sumAmt + :amount, rb.udt = CURRENT_TIMESTAMP WHERE rb.mIdx = :mIdx")
    int addAmount(@Param("mIdx") BigInteger mIdx, @Param("amount") BigDecimal amount);

    /**
     * Subtract amount from member's reward balance
     */
    @Modifying
    @Query("UPDATE RewardBalanceEntity rb SET rb.sumAmt = rb.sumAmt - :amount, rb.udt = CURRENT_TIMESTAMP WHERE rb.mIdx = :mIdx AND rb.sumAmt >= :amount")
    int subtractAmount(@Param("mIdx") BigInteger mIdx, @Param("amount") BigDecimal amount);

    /**
     * Get current balance amount for member
     */
    @Query("SELECT rb.sumAmt FROM RewardBalanceEntity rb WHERE rb.mIdx = :mIdx")
    Optional<BigDecimal> getBalanceAmount(@Param("mIdx") BigInteger mIdx);
}
