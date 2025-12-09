package com.audigo.audigo_back.scheduler;

import com.audigo.audigo_back.service.reward.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardScheduler {

    private final RewardService rewardService;

    /**
     * Process scheduled reward adjustments
     * Runs every hour at minute 0
     * Corresponds to Node.js: rewardAdjust() scheduler
     */
    @Scheduled(cron = "0 0 * * * *")
    public void processScheduledAdjustments() {
        log.info("Starting scheduled reward adjustments processing");
        try {
            rewardService.processScheduledAdjustments();
            log.info("Scheduled reward adjustments processed successfully");
        } catch (Exception e) {
            log.error("Error processing scheduled reward adjustments", e);
        }
    }

    /**
     * Process reward expiration
     * Runs on the 1st day of every month at 2:00 AM
     * Corresponds to Node.js: rewardExpireScheduler()
     */
    @Scheduled(cron = "0 2 1 * * *")
    public void processRewardExpiration() {
        log.info("Starting reward expiration processing");
        try {
            rewardService.processRewardExpiration();
            log.info("Reward expiration processed successfully");
        } catch (Exception e) {
            log.error("Error processing reward expiration", e);
        }
    }
}
