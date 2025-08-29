package com.audigo.audigo_back.dto.object;

import java.util.ArrayList;
import java.util.List;

import com.audigo.audigo_back.entity.BoardListViewEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardListItem {
    private int bIdx;
    private int aIdx;
    private String id;
    private String boardType;
    private String title;
    private String titleEn;
    private String content;
    private String contentEn;
    private String titleImage;
    private int favoriteCount;
    private int commentCount;
    private int viewCount;
    private String cdt;
    private String adminNm;

    public BoardListItem(BoardListViewEntity boardListViewEntity) {
        this.bIdx = boardListViewEntity.getBIdx();
        this.aIdx = boardListViewEntity.getAIdx();
        this.id = boardListViewEntity.getId();
        this.boardType = boardListViewEntity.getBoardType();
        this.title = boardListViewEntity.getTitle();
        this.titleEn = boardListViewEntity.getTitleEn();
        this.content = boardListViewEntity.getContent();
        this.contentEn = boardListViewEntity.getContentEn();
        this.titleImage = boardListViewEntity.getTitleImage();
        this.favoriteCount = boardListViewEntity.getFavoriteCnt();
        this.commentCount = boardListViewEntity.getCommentCnt();
        this.viewCount = boardListViewEntity.getViewCnt();
        this.cdt = boardListViewEntity.getCdt();
        this.adminNm = boardListViewEntity.getAdminNm();
    }

    public static List<BoardListItem> getList(List<BoardListViewEntity> boardListViewEntities) {
        List<BoardListItem> list = new ArrayList<>();
        for (BoardListViewEntity boardListViewEntity: boardListViewEntities) {
            BoardListItem boardListItem = new BoardListItem(boardListViewEntity);
            list.add(boardListItem);
        }
        return list;
    }
}