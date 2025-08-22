package com.audigo.audigo_back.dto.response.admin.board;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;
import com.audigo.audigo_back.dto.object.CommentListItem;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.repository.resultSet.GetCommentListResultSet;

import lombok.Getter;

@Getter
public class GetCommentListResponseDto extends ResponseDto {

    private List<CommentListItem> commentList;

    // GetCommentListResultSet interface injection 으로 list array 포함 생성자로 수정
    public GetCommentListResponseDto(List<GetCommentListResultSet> resultSets) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.commentList = CommentListItem.copyList(resultSets);
    }

    public static ResponseEntity<GetCommentListResponseDto> success(List<GetCommentListResultSet> resultSets) {
        GetCommentListResponseDto result = new GetCommentListResponseDto(resultSets);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistingContents() {
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTING_CONTENTS, ResponseMessage.NOT_EXISTING_CONTENTS);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

}
