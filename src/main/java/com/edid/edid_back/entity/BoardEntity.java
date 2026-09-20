package com.audigo.audigo_back.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import com.audigo.audigo_back.dto.request.admin.board.PostBoardRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "board")
@Table(name = "board")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "b_idx")
    private int bIdx;
    
    @Column(name = "a_idx")
    private int aIdx;
    
    @Column(name = "m_idx")
    private BigInteger mIdx;
    
    @Column(name = "board_type")
    private String boardType;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "content")
    private String content;
    
    @Column(name = "title_en")
    private String titleEn;
    
    @Column(name = "content_en")
    private String contentEn;
    
    @Column(name = "publish_dt")
    private Timestamp publishDt;
    
    @Column(name = "cdt")
    private Timestamp cdt;
    
    @Column(name = "udt")
    private Timestamp udt;
    
    @Column(name = "favorite_cnt")
    private int favoriteCnt;
    
    @Column(name = "comment_cnt")
    private int commentCnt;
    
    @Column(name = "view_cnt")
    private int viewCnt;

    /**
     * 관리자
     * @param dto
     * @param aIdx
     */
    public BoardEntity(PostBoardRequestDto dto, int aIdx) {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        this.aIdx = aIdx;
        this.boardType = dto.getBoardType();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.titleEn = dto.getTitleEn();
        this.contentEn = dto.getContentEn();
        try {
            this.publishDt = Timestamp.valueOf(dto.getPublishDt());
        } catch (IllegalArgumentException e) {
            log.info("=== Invalid timestamp format for publishDt: " + dto.getPublishDt() + ". Expected format: yyyy-MM-dd HH:mm:ss");
            throw new IllegalArgumentException("=== Invalid timestamp format for publishDt: ", e);
        }
        this.cdt = now;
        this.favoriteCnt = 0;
        this.commentCnt = 0;
        this.viewCnt = 0;
    }

    /**
     * 일반사용자
     * @param dto
     * @param mIdx
     */
    public BoardEntity(PostBoardRequestDto dto, BigInteger mIdx) {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        this.aIdx = 0;
        this.mIdx = mIdx;
        this.boardType = dto.getBoardType();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.titleEn = dto.getTitleEn();
        this.contentEn = dto.getContentEn();
        this.cdt = now;
        this.favoriteCnt = 0;
        this.commentCnt = 0;
        this.viewCnt = 0;
    }

    public void increaseViewCount() {
        this.viewCnt++;
    }

    public void increaseFavoriteCount() {
        this.favoriteCnt++;
    }

    public void increaseCommentCount() {
        this.commentCnt++;
    }

    public void decreaseFavoriteCount() {
        this.favoriteCnt--;
    }

}