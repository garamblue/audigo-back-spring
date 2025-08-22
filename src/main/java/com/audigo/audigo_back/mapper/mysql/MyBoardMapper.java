package com.audigo.audigo_back.mapper.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.audigo.audigo_back.entity.BoardEntity;

@Mapper
public interface MyBoardMapper {
    //Mybatis 목록 가져오기
    List<Map<String, Object>> selectAllBoard();

    //Mybatis 하나만 가져오기
    Map<String, Object> selectOneBoard(@Param("brdNum") Integer brdNum);
    
    //Mybatis 게시글 등록
    int insertBoard(BoardEntity boardEntity);
}
