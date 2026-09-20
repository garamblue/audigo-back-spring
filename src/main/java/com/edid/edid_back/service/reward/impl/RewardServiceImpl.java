package com.audigo.audigo_back.service.reward.impl;

import com.audigo.audigo_back.dto.request.reward.RewardAdjustRequestDto;
import com.audigo.audigo_back.dto.request.reward.RewardExchangeRequestDto;
import com.audigo.audigo_back.dto.request.reward.RewardTopupRequestDto;
import com.audigo.audigo_back.dto.response.reward.RewardBalanceResponseDto;
import com.audigo.audigo_back.dto.response.reward.RewardExpirationResponseDto;
import com.audigo.audigo_back.dto.response.reward.RewardHistoryResponseDto;
import com.audigo.audigo_back.entity.reward.*;
import com.audigo.audigo_back.repository.reward.*;
import com.audigo.audigo_back.service.reward.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardBalanceRepository balanceRepository;
    private final RewardTopupRepository topupRepository;
    private final RewardExchangeRepository exchangeRepository;
    private final RewardHistoryRepository historyRepository;
    private final RewardAdjustHistoryRepository adjustHistoryRepository;
    private final RewardPolicyRepository policyRepository;

    @Override
    @Transactional(readOnly = true)
    public RewardBalanceResponseDto getBalance(BigInteger mIdx) {
        BigDecimal balance = balanceRepository.findByMIdx(mIdx)
            .map(RewardBalanceEntity::getSumAmt)
            .orElse(BigDecimal.ZERO);

        return RewardBalanceResponseDto.success(balance);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BigDecimal addReward(RewardTopupRequestDto request) {
        log.info("Adding reward: mIdx={}, code={}, amount={}", request.getMIdx(), request.getCode(), request.getAmount());

        // 1. Create topup record
        RewardTopupEntity topup = new RewardTopupEntity();
        topup.setMIdx(request.getMIdx());
        topup.setCd(request.getCode());
        topup.setRAmt(request.getAmount());
        topup.setTranDt(LocalDateTime.now());
        RewardTopupEntity savedTopup = topupRepository.save(topup);

        // 2. Create history link (if source provided)
        if (request.getSourceTableIdx() != null && request.getSourceTableName() != null) {
            RewardHistoryEntity history = new RewardHistoryEntity();
            history.setRtIdx(savedTopup.getRtIdx());
            history.setTableIdx(request.getSourceTableIdx());
            history.setTableNm(request.getSourceTableName());
            historyRepository.save(history);
        }

        // 3. Update balance (add amount)
        RewardBalanceEntity balance = balanceRepository.findByMIdxWithLock(request.getMIdx())
            .orElseGet(() -> {
                RewardBalanceEntity newBalance = new RewardBalanceEntity();
                newBalance.setMIdx(request.getMIdx());
                newBalance.setSumAmt(BigDecimal.ZERO);
                return balanceRepository.save(newBalance);
            });

        balance.setSumAmt(balance.getSumAmt().add(request.getAmount()));
        balance.setUdt(LocalDateTime.now());
        balanceRepository.save(balance);

        log.info("Reward added successfully. New balance: {}", balance.getSumAmt());
        return balance.getSumAmt();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BigDecimal deductReward(RewardExchangeRequestDto request) {
        log.info("Deducting reward: mIdx={}, code={}, amount={}", request.getMIdx(), request.getCode(), request.getAmount());

        // 1. Check sufficient balance
        RewardBalanceEntity balance = balanceRepository.findByMIdxWithLock(request.getMIdx())
            .orElseThrow(() -> new RuntimeException("Reward balance not found for member: " + request.getMIdx()));

        if (balance.getSumAmt().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient reward balance. Current: " + balance.getSumAmt() + ", Required: " + request.getAmount());
        }

        // 2. Create exchange record
        RewardExchangeEntity exchange = new RewardExchangeEntity();
        exchange.setMIdx(request.getMIdx());
        exchange.setCd(request.getCode());
        exchange.setRAmt(request.getAmount());
        exchange.setTableIdx(request.getSourceTableIdx());
        exchange.setTableNm(request.getSourceTableName());
        exchange.setTranDt(LocalDateTime.now());
        exchangeRepository.save(exchange);

        // 3. Update balance (subtract amount)
        balance.setSumAmt(balance.getSumAmt().subtract(request.getAmount()));
        balance.setUdt(LocalDateTime.now());
        balanceRepository.save(balance);

        log.info("Reward deducted successfully. New balance: {}", balance.getSumAmt());
        return balance.getSumAmt();
    }

    @Override
    @Transactional(readOnly = true)
    public RewardHistoryResponseDto getRewardHistory(BigInteger mIdx, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Get current balance
        BigDecimal currentBalance = balanceRepository.findByMIdx(mIdx)
            .map(RewardBalanceEntity::getSumAmt)
            .orElse(BigDecimal.ZERO);

        // Get topups
        List<RewardTopupEntity> topups = topupRepository.findByMIdxAndDateRange(mIdx, startDate, endDate);

        // Get exchanges
        List<RewardExchangeEntity> exchanges = exchangeRepository.findByMIdxAndDateRange(mIdx, startDate, endDate);

        // Merge and sort
        List<RewardHistoryResponseDto.RewardTransactionDto> transactions = new ArrayList<>();

        for (RewardTopupEntity topup : topups) {
            String description = getRewardDescription(topup.getCd());
            transactions.add(new RewardHistoryResponseDto.RewardTransactionDto(
                topup.getCd(),
                description,
                topup.getRAmt(),
                topup.getTranDt(),
                "topup"
            ));
        }

        for (RewardExchangeEntity exchange : exchanges) {
            String description = getRewardDescription(exchange.getCd());
            transactions.add(new RewardHistoryResponseDto.RewardTransactionDto(
                exchange.getCd(),
                description,
                exchange.getRAmt().negate(), // Negative for exchanges
                exchange.getTranDt(),
                "exchange"
            ));
        }

        // Sort by date descending
        transactions.sort((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()));

        return new RewardHistoryResponseDto(currentBalance, transactions, 1, transactions.size());
    }

    @Override
    @Transactional(readOnly = true)
    public RewardExpirationResponseDto getScheduledExpiration(BigInteger mIdx) {
        // Calculate rewards expiring in 1 month
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1).plusMonths(1);

        // Sum topups from that month
        BigDecimal topupAmount = topupRepository.sumByMIdxAndMonth(mIdx, oneYearAgo);

        // Sum exchanges after that month
        BigDecimal exchangeAmount = exchangeRepository.sumByMIdxAfterDate(mIdx, oneYearAgo);

        // Calculate expiring amount
        BigDecimal expiringAmount = topupAmount.subtract(exchangeAmount);
        if (expiringAmount.compareTo(BigDecimal.ZERO) < 0) {
            expiringAmount = BigDecimal.ZERO;
        }

        return RewardExpirationResponseDto.of(expiringAmount);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createAdjustment(RewardAdjustRequestDto request, BigInteger adminIdx) {
        log.info("Creating reward adjustment: type={}, mIdx={}, amount={}", request.getType(), request.getMIdx(), request.getAmount());

        // 1. Create adjustment history record
        RewardAdjustHistoryEntity adjustment = new RewardAdjustHistoryEntity();
        adjustment.setMIdx(request.getMIdx());
        adjustment.setCd(request.getCode());
        adjustment.setType(request.getType());
        adjustment.setRAmt(request.getAmount());
        adjustment.setTranDt(request.getScheduledDate());
        adjustment.setCAidx(adminIdx);
        RewardAdjustHistoryEntity savedAdjustment = adjustHistoryRepository.save(adjustment);

        // 2. If not scheduled (immediate processing)
        if (request.getScheduledDate() == null || request.getScheduledDate().isBefore(LocalDateTime.now())) {
            processAdjustment(savedAdjustment);
        } else {
            log.info("Adjustment scheduled for: {}", request.getScheduledDate());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processScheduledAdjustments() {
        log.info("Processing scheduled adjustments...");

        List<RewardAdjustHistoryEntity> adjustments = adjustHistoryRepository.findAdjustmentsToProcess();

        for (RewardAdjustHistoryEntity adjustment : adjustments) {
            try {
                processAdjustment(adjustment);
                log.info("Processed scheduled adjustment: {}", adjustment.getRahIdx());
            } catch (Exception e) {
                log.error("Failed to process adjustment: {}", adjustment.getRahIdx(), e);
            }
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processRewardExpiration() {
        log.info("Processing reward expiration...");

        // Implementation similar to Node.js rewardExpireScheduler
        // This would need a native query to calculate expired rewards by month
        // For now, this is a placeholder

        log.info("Reward expiration process completed");
    }

    @Override
    @Transactional
    public void initializeMemberBalance(BigInteger mIdx) {
        if (!balanceRepository.existsByMIdx(mIdx)) {
            RewardBalanceEntity balance = new RewardBalanceEntity();
            balance.setMIdx(mIdx);
            balance.setSumAmt(BigDecimal.ZERO);
            balanceRepository.save(balance);
            log.info("Initialized reward balance for member: {}", mIdx);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(BigInteger mIdx, BigDecimal amount) {
        BigDecimal balance = balanceRepository.getBalanceAmount(mIdx)
            .orElse(BigDecimal.ZERO);
        return balance.compareTo(amount) >= 0;
    }

    // Private helper methods

    private void processAdjustment(RewardAdjustHistoryEntity adjustment) {
        String type = adjustment.getType();

        if ("P".equals(type)) {
            // Topup (Plus)
            RewardTopupRequestDto topupRequest = new RewardTopupRequestDto();
            topupRequest.setMIdx(adjustment.getMIdx());
            topupRequest.setCode(adjustment.getCd());
            topupRequest.setAmount(adjustment.getRAmt());
            topupRequest.setSourceTableIdx(adjustment.getRahIdx());
            topupRequest.setSourceTableName(RewardTableCode.REWARD_ADJUST.getCode());
            addReward(topupRequest);

        } else if ("M".equals(type) || "E".equals(type)) {
            // Exchange (Minus) or Expired
            RewardExchangeRequestDto exchangeRequest = new RewardExchangeRequestDto();
            exchangeRequest.setMIdx(adjustment.getMIdx());
            exchangeRequest.setCode(adjustment.getCd());
            exchangeRequest.setAmount(adjustment.getRAmt());
            exchangeRequest.setSourceTableIdx(adjustment.getRahIdx());
            exchangeRequest.setSourceTableName(RewardTableCode.REWARD_ADJUST.getCode());
            deductReward(exchangeRequest);
        }
    }

    private String getRewardDescription(String code) {
        return policyRepository.findFirstByCd(code)
            .map(RewardPolicyEntity::getDescr)
            .orElse(code);
    }
}
