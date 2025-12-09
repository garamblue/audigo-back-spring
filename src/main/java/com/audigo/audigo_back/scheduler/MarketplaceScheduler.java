package com.audigo.audigo_back.scheduler;

import com.audigo.audigo_back.service.market.MarketplaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketplaceScheduler {

    private final MarketplaceService marketplaceService;

    /**
     * Sync products and brands from GiftiShow
     * Runs daily at 15:05 (3:05 PM)
     * Corresponds to Node.js: get_giftishow() scheduler
     */
    @Scheduled(cron = "0 5 15 * * *")
    public void syncGiftishow() {
        log.info("Starting GiftiShow synchronization scheduler");
        try {
            marketplaceService.syncFromGiftishow();
            log.info("GiftiShow synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error in GiftiShow synchronization", e);
        }
    }
}
