package com.audigo.audigo_back.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.audigo.audigo_back.dto.request.admin.board.PostBoardRequestDto;
import com.audigo.audigo_back.dto.request.admin.board.PostCommentRequestDto;
import com.audigo.audigo_back.dto.response.CommonResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetBoardResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetCommentListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetFavoriteListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetLatestBoardListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetPaginationResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PostBoardResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PostCommentResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PutFavoriteResponseDto;

public interface BoardService {
    // 게시물 선택 시 해당 게시물 상세조회
    ResponseEntity<? super GetBoardResponseDto> getBoard(Integer bIdx);

    // 좋아요 목록 조회
    ResponseEntity<? super GetFavoriteListResponseDto> getFavoriteList(Integer bIdx);

    // 댓글 목록 조회
    ResponseEntity<? super GetCommentListResponseDto> getCommentList(Integer bIdx);

    // 관리자 게시물 등록
    ResponseEntity<? super PostBoardResponseDto> postBoardAdmin(PostBoardRequestDto dto, Integer aIdx);

    // 관리자 게시물 등록 mybatis version
    ResponseEntity<? super PostBoardResponseDto> postBoardAdmin_my(PostBoardRequestDto dto, String id);

    // 좋아요 등록
    ResponseEntity<? super PutFavoriteResponseDto> putFavorite(Integer bIdx, String email);

    // 댓글 등록
    ResponseEntity<? super PostCommentResponseDto> postComment(PostCommentRequestDto dto, Integer bIdx,
            String email);

    // 최근 게시물 조회
    ResponseEntity<? super GetLatestBoardListResponseDto> getLatestBoardList();

    // 페이징 처리된 게시물 조회
    ResponseEntity<? super GetPaginationResponseDto> getPagedList(int page, int countPerPage, boolean isPaged);

    // 게시물 조회
    CommonResponseDto<List<Map<String, Object>>> getAllBoard();

    // 게시물 하나 조회
    CommonResponseDto<Map<String, Object>> getOneBoard(Integer bIdx);

}
