package com.audigo.audigo_back.dto.response.admin.board;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.object.BoardListItem;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.entity.BoardListViewEntity;

import lombok.Getter;

@Getter
public class GetLatestBoardListResponseDto extends ResponseDto {

    private List<BoardListItem> latestBoardList;

    public GetLatestBoardListResponseDto(List<BoardListViewEntity> boardEntities) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.latestBoardList = BoardListItem.getList(boardEntities);
    }
    
    public static ResponseEntity<GetLatestBoardListResponseDto> success(List<BoardListViewEntity> boardEntities) {
        GetLatestBoardListResponseDto result = new GetLatestBoardListResponseDto(boardEntities);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    

    
}
