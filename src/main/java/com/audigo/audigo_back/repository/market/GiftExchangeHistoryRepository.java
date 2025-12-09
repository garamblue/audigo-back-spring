package com.audigo.audigo_back.repository.market;

import com.audigo.audigo_back.entity.market.GiftExchangeHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftExchangeHistoryRepository extends JpaRepository<GiftExchangeHistoryEntity, BigInteger> {

    @Query("SELECT geh FROM GiftExchangeHistoryEntity geh WHERE geh.mIdx = :mIdx ORDER BY geh.cdt DESC")
    Page<GiftExchangeHistoryEntity> findByMIdxOrderByCdtDesc(@Param("mIdx") BigInteger mIdx, Pageable pageable);

    Optional<GiftExchangeHistoryEntity> findByTrId(String trId);

    @Query("SELECT COUNT(geh) FROM GiftExchangeHistoryEntity geh WHERE geh.trId = :trId AND geh.retranYn = 'Y'")
    long countResendsByTrId(@Param("trId") String trId);

    @Query("SELECT geh FROM GiftExchangeHistoryEntity geh WHERE geh.mIdx = :mIdx AND geh.status = :status")
    List<GiftExchangeHistoryEntity> findByMIdxAndStatus(@Param("mIdx") BigInteger mIdx, @Param("status") String status);

    Page<GiftExchangeHistoryEntity> findByStatusOrderByCdtDesc(String status, Pageable pageable);

    @Query(value = "SELECT generate_trade_id()", nativeQuery = true)
    String generateTradeId();
}
