package com.audigo.audigo_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "board_list_view")
@Table(name = "board_list_view")
public class BoardListViewEntity {
    @Id
    @Column(name = "b_idx")
    private int bIdx;
    @Column(name = "a_idx")
    private int aIdx;
    @Column(name = "id")
    private String id;
    private String title;
    @Column(name = "title_en")
    private String titleEn;
    private String content;
    @Column(name = "content_en")
    private String contentEn;
    @Column(name = "title_image")
    private String titleImage;
    @Column(name = "view_cnt")
    private int viewCnt;
    @Column(name = "favorite_cnt")
    private int favoriteCnt;
    @Column(name = "comment_cnt")
    private int commentCnt;
    @Column(name = "board_type")
    private String boardType;
    private String cdt;
    @Column(name = "name")
    private String adminNm;
}
