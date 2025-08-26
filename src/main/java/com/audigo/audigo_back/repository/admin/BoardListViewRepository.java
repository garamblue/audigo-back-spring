package com.audigo.audigo_back.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.BoardListViewEntity;

@Repository
public interface BoardListViewRepository extends JpaRepository<BoardListViewEntity, Integer> {

    List<BoardListViewEntity> findAllByOrderByCdtDesc();

}
