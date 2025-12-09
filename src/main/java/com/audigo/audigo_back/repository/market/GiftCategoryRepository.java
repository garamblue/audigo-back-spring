package com.audigo.audigo_back.repository.market;

import com.audigo.audigo_back.entity.market.GiftCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface GiftCategoryRepository extends JpaRepository<GiftCategoryEntity, BigInteger> {
    List<GiftCategoryEntity> findByVisibleOrderByCategory1SeqAsc(String visible);
}
