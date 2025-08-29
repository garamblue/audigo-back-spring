package com.audigo.audigo_back.dto.response.admin.board;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.object.BoardListItem;
import com.audigo.audigo_back.dto.object.Pagination;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.entity.BoardListViewEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class GetPaginationResponseDto extends ResponseDto {
    
    private Pagination pagination; //record type
    
    @JsonProperty("data")
    private List<BoardListItem> data;

    public GetPaginationResponseDto(Page<BoardListViewEntity> boardPage) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.data = BoardListItem.getList(boardPage.getContent());
        this.pagination = Pagination.of(
            //+ 1을 하는 이유는 Spring Data JPA의 페이지 번호 체계가 0부터 시작하기 때문
            boardPage.getNumber() + 1,
            boardPage.getSize(),
            boardPage.getTotalElements()
        );
    }
    
    public static ResponseEntity<GetPaginationResponseDto> success(Page<BoardListViewEntity> boardPage) {
        GetPaginationResponseDto result = new GetPaginationResponseDto(boardPage);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}