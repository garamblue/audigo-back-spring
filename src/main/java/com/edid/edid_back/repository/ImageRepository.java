package com.audigo.audigo_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    // Custom query methods (if needed) can be defined here
    List<ImageEntity> findBybIdx(Integer bIdx);
}
