package com.edid.edid_back.repository.market;

import com.edid.edid_back.entity.market.GiftCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface GiftCategoryRepository extends JpaRepository<GiftCategoryEntity, BigInteger> {
    List<GiftCategoryEntity> findByVisibleOrderByCategory1SeqAsc(String visible);
}
