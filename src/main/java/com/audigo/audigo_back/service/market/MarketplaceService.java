package com.audigo.audigo_back.service.market;

import com.audigo.audigo_back.dto.request.market.ExchangeRequestDto;
import com.audigo.audigo_back.dto.request.reward.RewardExchangeRequestDto;
import com.audigo.audigo_back.dto.response.market.ProductListResponseDto;
import com.audigo.audigo_back.entity.market.*;
import com.audigo.audigo_back.entity.reward.RewardExchangeCode;
import com.audigo.audigo_back.entity.reward.RewardTableCode;
import com.audigo.audigo_back.repository.market.GiftProductRepository;
import com.audigo.audigo_back.repository.market.GiftExchangeHistoryRepository;
import com.audigo.audigo_back.repository.market.GiftBalanceRepository;
import com.audigo.audigo_back.repository.market.GiftBrandRepository;
import com.audigo.audigo_back.repository.market.GiftCategoryRepository;
import com.audigo.audigo_back.service.reward.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final GiftProductRepository productRepository;
    private final GiftExchangeHistoryRepository exchangeHistoryRepository;
    private final GiftBalanceRepository balanceRepository;
    private final GiftBrandRepository brandRepository;
    private final GiftCategoryRepository categoryRepository;

    private final GiftishowApiService giftishowApiService;
    private final RewardService rewardService;

    /**
     * Get product list
     */
    @Transactional(readOnly = true)
    public ProductListResponseDto getProductList(Pageable pageable) {
        Page<GiftProductEntity> page = productRepository.findByVisibleAndUseYnOrderByGpIdxDesc("Y", "Y", pageable);

        List<ProductListResponseDto.ProductDto> products = page.getContent().stream()
            .map(p -> new ProductListResponseDto.ProductDto(
                p.getGpIdx(), p.getGoodsCode(), p.getGoodsName(), p.getBrandCode(),
                p.getRealPrice(), p.getAppPrice(), p.getGoodsImgs(), p.getGoodsImgb(),
                p.getLimitDay(), p.getValidPrdDay()
            ))
            .collect(Collectors.toList());

        return new ProductListResponseDto(products, page.getTotalPages(), page.getTotalElements());
    }

    /**
     * Exchange reward for gift (Main business logic)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exchangeGift(BigInteger mIdx, ExchangeRequestDto request) {
        log.info("Starting gift exchange: mIdx={}, goodsCode={}", mIdx, request.getGoodsCode());

        try {
            // 1. Get product info
            GiftProductEntity product = productRepository.findById(request.getGpIdx())
                .orElseThrow(() -> new RuntimeException("Product not found"));

            // 2. Check balance
            if (!rewardService.hasSufficientBalance(mIdx, product.getAppPrice())) {
                throw new RuntimeException("Insufficient reward balance");
            }

            // 3. Generate trade ID
            String trId = exchangeHistoryRepository.generateTradeId();

            // 4. Create exchange history (pending)
            GiftExchangeHistoryEntity history = new GiftExchangeHistoryEntity();
            history.setMIdx(mIdx);
            history.setGpIdx(product.getGpIdx());
            history.setGoodsCode(product.getGoodsCode());
            history.setGoodsName(product.getGoodsName());
            history.setBrandCode(product.getBrandCode());
            history.setRAmt(product.getAppPrice());
            history.setRealPrice(product.getRealPrice().toString());
            history.setLimitDay(product.getLimitDay());
            history.setValidPrdDay(product.getValidPrdDay());
            history.setMobileNum(request.getMobileNum());  // TODO: Encrypt
            history.setTrId(trId);
            history.setReqDt(LocalDateTime.now());
            history.setStatus("P");  // Pending
            exchangeHistoryRepository.save(history);

            // 5. Call GiftiShow API to send coupon
            Map<String, Object> apiResponse = giftishowApiService.sendCoupon(
                product.getGoodsCode(),
                request.getMobileNum(),
                trId
            );

            // 6. Update exchange status to success
            if (apiResponse != null && apiResponse.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) apiResponse.get("result");
                String orderNo = result.get("orderNo").toString();

                history.setOrderNo(orderNo);
                history.setStatus("S");  // Success
                history.setResDt(LocalDateTime.now());
                exchangeHistoryRepository.save(history);
            } else {
                history.setStatus("F");  // Failed
                exchangeHistoryRepository.save(history);
                throw new RuntimeException("GiftiShow API call failed");
            }

            // 7. Deduct reward balance
            RewardExchangeRequestDto rewardRequest = new RewardExchangeRequestDto();
            rewardRequest.setMIdx(mIdx);
            rewardRequest.setCode(RewardExchangeCode.GIFTISHOW.getCode());
            rewardRequest.setAmount(product.getAppPrice());
            rewardRequest.setSourceTableIdx(history.getGehIdx());
            rewardRequest.setSourceTableName(RewardTableCode.GIFTISHOW.getCode());
            rewardService.deductReward(rewardRequest);

            // 8. Check and store business account balance
            checkAndStoreBalance();

            log.info("Gift exchange completed successfully: trId={}", trId);
            return trId;

        } catch (Exception e) {
            log.error("Gift exchange failed: {}", e.getMessage(), e);
            throw new RuntimeException("Exchange failed: " + e.getMessage(), e);
        }
    }

    /**
     * Resend coupon
     */
    @Transactional
    public void resendCoupon(BigInteger mIdx, BigInteger gehIdx) {
        log.info("Resending coupon: mIdx={}, gehIdx={}", mIdx, gehIdx);

        GiftExchangeHistoryEntity original = exchangeHistoryRepository.findById(gehIdx)
            .orElseThrow(() -> new RuntimeException("Exchange history not found"));

        // Check resend limit (max 3)
        long resendCount = exchangeHistoryRepository.countResendsByTrId(original.getTrId());
        if (resendCount >= 3) {
            throw new RuntimeException("Resend limit exceeded (max 3)");
        }

        // Call GiftiShow resend API
        giftishowApiService.resendCoupon(original.getTrId());

        // Create new history record
        GiftExchangeHistoryEntity resend = new GiftExchangeHistoryEntity();
        resend.setMIdx(original.getMIdx());
        resend.setGpIdx(original.getGpIdx());
        resend.setGoodsCode(original.getGoodsCode());
        resend.setGoodsName(original.getGoodsName());
        resend.setBrandCode(original.getBrandCode());
        resend.setRAmt(original.getRAmt());
        resend.setRealPrice(original.getRealPrice());
        resend.setLimitDay(original.getLimitDay());
        resend.setValidPrdDay(original.getValidPrdDay());
        resend.setMobileNum(original.getMobileNum());
        resend.setTrId(original.getTrId());
        resend.setOrderNo(original.getOrderNo());
        resend.setStatus("S");
        resend.setRetranYn("Y");
        resend.setReqDt(LocalDateTime.now());
        resend.setResDt(LocalDateTime.now());
        exchangeHistoryRepository.save(resend);
    }

    /**
     * Sync products and brands from GiftiShow (Scheduler)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void syncFromGiftishow() {
        log.info("Starting GiftiShow synchronization");

        try {
            // 1. Sync brands
            Map<String, Object> brandsResponse = giftishowApiService.syncBrands();
            // TODO: Process and save brands

            // 2. Sync goods
            Map<String, Object> goodsResponse = giftishowApiService.syncGoods();
            // TODO: Process and save goods

            // 3. Delete obsolete products
            int deletedProducts = productRepository.deleteObsoleteProducts(LocalDate.now());
            log.info("Deleted {} obsolete products", deletedProducts);

            log.info("GiftiShow synchronization completed");
        } catch (Exception e) {
            log.error("GiftiShow sync error: {}", e.getMessage(), e);
        }
    }

    private void checkAndStoreBalance() {
        try {
            BigDecimal balance = giftishowApiService.getAccountBalance();
            int status = determineBalanceStatus(balance);

            GiftBalanceEntity balanceEntity = new GiftBalanceEntity();
            balanceEntity.setBalanceAmt(balance);
            balanceEntity.setStatus(status);
            balanceRepository.save(balanceEntity);

            if (status > 0) {
                log.warn("GiftiShow account balance warning: balance={}, status={}", balance, status);
                // TODO: Send SMS alert to admin
            }
        } catch (Exception e) {
            log.error("Error checking balance: {}", e.getMessage());
        }
    }

    private int determineBalanceStatus(BigDecimal balance) {
        if (balance.compareTo(new BigDecimal("2000000")) > 0) {
            return 0;  // OK
        } else if (balance.compareTo(new BigDecimal("500000")) > 0) {
            return 1;  // WARNING
        } else {
            return 2;  // CRITICAL
        }
    }
}
