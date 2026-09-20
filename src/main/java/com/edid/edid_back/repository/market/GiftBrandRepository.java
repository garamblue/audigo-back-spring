package com.audigo.audigo_back.repository.market;

import com.audigo.audigo_back.entity.market.GiftBrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftBrandRepository extends JpaRepository<GiftBrandEntity, String> {
    Optional<GiftBrandEntity> findByBrandCode(String brandCode);
    List<GiftBrandEntity> findByCategory1Seq(BigInteger category1Seq);
}
