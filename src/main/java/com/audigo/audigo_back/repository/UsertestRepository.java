package com.audigo.audigo_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.UsertestEntity;

//PK type is String
@Repository
public interface UsertestRepository extends JpaRepository<UsertestEntity, Integer> {
    boolean existsByUsername(String username);
}