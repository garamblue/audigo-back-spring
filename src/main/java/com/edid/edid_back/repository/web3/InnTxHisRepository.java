package com.audigo.audigo_back.repository.web3;

import com.audigo.audigo_back.entity.web3.InnTxHisEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InnTxHisRepository extends JpaRepository<InnTxHisEntity, Long> {

    /**
     * 트랜잭션 해시로 조회
     */
    Optional<InnTxHisEntity> findByTxHash(String txHash);

    /**
     * 지갑 ID로 거래 내역 조회 (페이징)
     */
    Page<InnTxHisEntity> findByEwIdxOrderByCdtDesc(Long ewIdx, Pageable pageable);

    /**
     * 지갑 ID와 상태로 조회
     */
    List<InnTxHisEntity> findByEwIdxAndStatus(Long ewIdx, String status);

    /**
     * pending 상태인 트랜잭션 조회
     */
    @Query("SELECT i FROM InnTxHisEntity i WHERE i.status = 'pending'")
    List<InnTxHisEntity> findPendingTransactions();
}
