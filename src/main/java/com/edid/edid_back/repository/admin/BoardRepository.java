package com.audigo.audigo_back.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.BoardEntity;
import com.audigo.audigo_back.repository.resultSet.GetBoardResultSet;

//PK type is Integer
@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    boolean existsBybIdx(Integer bIdx);

    BoardEntity findBybIdx(Integer bIdx);

    /**
     * BoardRepository 인스턴스로 쿼리 내부에서 select 후 update 같은 sql transaction도 가능.
     * @param bIdx
     * @return
     */
    @Query(value = "SELECT " +
                "b.b_idx AS bIdx, " +
                "b.a_idx AS aIdx, " +
                "b.board_type AS boardType, " +
                "TO_CHAR(b.publish_dt, 'YYYY-MM-DD HH24:MI:SS') AS publishDt, " +
                "b.title AS title, " +
                "b.content AS content, " +
                "b.title_en AS titleEn, " +
                "b.content_en AS contentEn, " +
                "TO_CHAR(b.cdt, 'YYYY-MM-DD HH24:MI:SS') AS cdt, " +
                "a.id AS adminId, " +
                "a.nm AS adminNm, " +
                "i.image_url AS imageUrl " +
            "FROM board b " +
            "JOIN users.admin a ON b.a_idx = a.a_idx " +
            "LEFT JOIN image i ON b.b_idx = i.b_idx " +
            "WHERE b.b_idx = ?1", nativeQuery = true)
    GetBoardResultSet getBoard(Integer bIdx);
}