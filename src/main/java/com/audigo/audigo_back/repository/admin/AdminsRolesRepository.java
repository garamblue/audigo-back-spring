package com.audigo.audigo_back.repository.admin;

import com.audigo.audigo_back.entity.admin.AdminsRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsRolesRepository extends JpaRepository<AdminsRolesEntity, BigInteger> {

    /**
     * 역할 코드로 역할 조회
     */
    Optional<AdminsRolesEntity> findByRoleCd(String roleCd);

    /**
     * 활성 역할 목록 조회
     */
    List<AdminsRolesEntity> findByStts(String stts);

    /**
     * 역할 코드 중복 확인
     */
    boolean existsByRoleCd(String roleCd);
}
