package com.audigo.audigo_back.service.reward;

import com.audigo.audigo_back.dto.request.reward.RewardAdjustRequestDto;
import com.audigo.audigo_back.dto.request.reward.RewardExchangeRequestDto;
import com.audigo.audigo_back.dto.request.reward.RewardTopupRequestDto;
import com.audigo.audigo_back.dto.response.reward.RewardBalanceResponseDto;
import com.audigo.audigo_back.dto.response.reward.RewardExpirationResponseDto;
import com.audigo.audigo_back.dto.response.reward.RewardHistoryResponseDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

public interface RewardService {

    /**
     * Get current reward balance for member
     */
    RewardBalanceResponseDto getBalance(BigInteger mIdx);

    /**
     * Add rewards to member balance (topup)
     * @return New balance after topup
     */
    BigDecimal addReward(RewardTopupRequestDto request);

    /**
     * Deduct rewards from member balance (exchange)
     * @return New balance after exchange
     */
    BigDecimal deductReward(RewardExchangeRequestDto request);

    /**
     * Get reward transaction history for member
     */
    RewardHistoryResponseDto getRewardHistory(BigInteger mIdx, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Get rewards scheduled to expire in 1 month
     */
    RewardExpirationResponseDto getScheduledExpiration(BigInteger mIdx);

    /**
     * Admin: Create reward adjustment (immediate or scheduled)
     */
    void createAdjustment(RewardAdjustRequestDto request, BigInteger adminIdx);

    /**
     * Process scheduled adjustments (called by scheduler)
     */
    void processScheduledAdjustments();

    /**
     * Process reward expiration (called by scheduler)
     */
    void processRewardExpiration();

    /**
     * Initialize reward balance for new member
     */
    void initializeMemberBalance(BigInteger mIdx);

    /**
     * Check if member has sufficient balance
     */
    boolean hasSufficientBalance(BigInteger mIdx, BigDecimal amount);
}
