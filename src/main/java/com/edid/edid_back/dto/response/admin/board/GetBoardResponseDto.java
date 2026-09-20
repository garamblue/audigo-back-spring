package com.audigo.audigo_back.dto.response.admin.board;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.entity.ImageEntity;
import com.audigo.audigo_back.repository.resultSet.GetBoardResultSet;

import lombok.Getter;

@Getter
public class GetBoardResponseDto extends ResponseDto {
    //GetBoardResponseDto Data_Transfer_Object
    private int bIdx;
    private int aIdx;
    private String boardType;
    private String title;
    private String titleEn;
    private String content;
    private String contentEn;
    private List<String> boardImageList;
    private String publishDt;
    private String cdt;
    private String adminNm;

    public GetBoardResponseDto(GetBoardResultSet resultSet, List<ImageEntity> imageEntities) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);

        List<String> boardImageList = new ArrayList<>();
        for (ImageEntity imageEntity : imageEntities) {
            String boardImage = imageEntity.getImageUrl();
            boardImageList.add(boardImage);
        }

        this.bIdx = resultSet.getBIdx();
        this.aIdx = resultSet.getAIdx();
        this.boardType = resultSet.getBoardType();
        this.publishDt = resultSet.getPublishDt();
        this.title = resultSet.getTitle();
        this.titleEn = resultSet.getTitleEn();
        this.content = resultSet.getContent();
        this.contentEn = resultSet.getContentEn();
        this.boardImageList = boardImageList;
        this.cdt = resultSet.getCdt();
        this.adminNm = resultSet.getAdminNm();
    }

    public static ResponseEntity<GetBoardResponseDto> success(GetBoardResultSet resultSet,
            List<ImageEntity> imageEntities) {
        GetBoardResponseDto result = new GetBoardResponseDto(resultSet, imageEntities);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistingContents() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTING_CONTENTS, ResponseMessage.NOT_EXISTING_CONTENTS);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

}
