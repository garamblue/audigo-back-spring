package com.audigo.audigo_back.repository.market;

import com.audigo.audigo_back.entity.market.GiftBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface GiftBalanceRepository extends JpaRepository<GiftBalanceEntity, BigInteger> {

    @Query(value = "SELECT * FROM store.gift_balance ORDER BY cdt DESC LIMIT 1", nativeQuery = true)
    Optional<GiftBalanceEntity> findLatest();
}
