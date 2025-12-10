package com.audigo.audigo_back.repository.external;

import com.audigo.audigo_back.entity.external.PincruxOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Pincrux 오퍼월 Repository
 */
@Repository
public interface PincruxOfferRepository extends JpaRepository<PincruxOfferEntity, Long> {

    /**
     * 특정 유저의 Pincrux 이력 조회
     */
    @Query("SELECT p FROM PincruxOfferEntity p WHERE p.usrkey = :usrkey ORDER BY p.cdt DESC")
    List<PincruxOfferEntity> findByUsrkey(@Param("usrkey") String usrkey);

    /**
     * 거래 ID로 조회 (중복 체크용)
     */
    @Query("SELECT p FROM PincruxOfferEntity p WHERE p.transid = :transid")
    Optional<PincruxOfferEntity> findByTransid(@Param("transid") String transid);
}
