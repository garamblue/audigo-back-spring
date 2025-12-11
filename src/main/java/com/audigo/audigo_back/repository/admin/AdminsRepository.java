package com.audigo.audigo_back.repository.admin;

import com.audigo.audigo_back.entity.admin.AdminsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsRepository extends JpaRepository<AdminsEntity, BigInteger> {

    /**
     * ID로 관리자 조회 (암호화된 ID)
     */
    Optional<AdminsEntity> findById(String id);

    /**
     * ID와 활성 상태로 관리자 조회
     */
    @Query("SELECT a FROM AdminsEntity a WHERE a.id = :id AND a.actYn = 'Y'")
    Optional<AdminsEntity> findByIdAndActive(@Param("id") String id);

    /**
     * 역할 코드로 관리자 목록 조회
     */
    List<AdminsEntity> findByRoleCd(String roleCd);

    /**
     * 활성 관리자 목록 조회
     */
    List<AdminsEntity> findByActYn(String actYn);

    /**
     * ID 중복 확인
     */
    boolean existsById(String id);
}
