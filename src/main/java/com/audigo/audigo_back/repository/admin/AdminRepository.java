package com.audigo.audigo_back.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.AdminEntity;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {

    boolean existsById(String id);
    
    AdminEntity findById(String id);

    boolean existsByaIdx(Integer aIdx);

    AdminEntity findByaIdx(Integer aIdx);

    @Query(value = "SELECT a_idx FROM users.admin WHERE id = :id", nativeQuery = true)
    Integer findAIdxById(@Param("id") String id);
    
}