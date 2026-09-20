package com.edid.edid_back.repository.admin;

import com.edid.edid_back.entity.admin.AdminsMenusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsMenusRepository extends JpaRepository<AdminsMenusEntity, BigInteger> {

    /**
     * 메뉴 코드로 메뉴 조회
     */
    Optional<AdminsMenusEntity> findByMenuCd(String menuCd);

    /**
     * 활성 메뉴 목록 조회
     */
    List<AdminsMenusEntity> findByStts(String stts);

    /**
     * 1차 메뉴로 메뉴 목록 조회
     */
    List<AdminsMenusEntity> findByDepth1(String depth1);

    /**
     * 메뉴 코드 중복 확인
     */
    boolean existsByMenuCd(String menuCd);
}
