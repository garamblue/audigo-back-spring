package com.audigo.audigo_back.repository.market;

import com.audigo.audigo_back.entity.market.GiftProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftProductRepository extends JpaRepository<GiftProductEntity, BigInteger> {

    Optional<GiftProductEntity> findByGoodsCode(String goodsCode);

    @Query("SELECT gp FROM GiftProductEntity gp WHERE gp.visible = :visible AND gp.useYn = :useYn ORDER BY gp.gpIdx DESC")
    List<GiftProductEntity> findByVisibleAndUseYnOrderByGpIdxDesc(@Param("visible") String visible, @Param("useYn") String useYn);

    @Query("SELECT gp FROM GiftProductEntity gp WHERE gp.visible = :visible AND gp.useYn = :useYn ORDER BY gp.gpIdx DESC")
    Page<GiftProductEntity> findByVisibleAndUseYnOrderByGpIdxDesc(@Param("visible") String visible, @Param("useYn") String useYn, Pageable pageable);

    List<GiftProductEntity> findByBrandCodeAndVisibleAndUseYn(String brandCode, String visible, String useYn);

    List<GiftProductEntity> findByCategory1SeqAndVisibleAndUseYn(BigInteger category1Seq, String visible, String useYn);

    @Modifying
    @Query(value = "DELETE FROM store.gift_product WHERE DATE(udt) < :date", nativeQuery = true)
    int deleteObsoleteProducts(@Param("date") LocalDate date);

    boolean existsByGoodsCode(String goodsCode);
}
