package com.audigo.audigo_back.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.audigo.audigo_back.dto.request.admin.board.PostBoardRequestDto;
import com.audigo.audigo_back.dto.request.admin.board.PostCommentRequestDto;
import com.audigo.audigo_back.dto.response.CommonResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetBoardResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetLatestBoardListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetPaginationResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetCommentListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetFavoriteListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PostBoardResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PostCommentResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PutFavoriteResponseDto;
import com.audigo.audigo_back.service.admin.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@Tag(name = "Board API", description = "공지사항 & 게시판 API")
@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    //실제 DI: dependency injection 하는 객체 BoardService boardService = new BoardServiceImpl(...);
    private final BoardService boardService;
    
    /**
     * 선택한 게시물 조회
     * @param bIdx
     * @return
     */
    @Operation(summary = "선택 게시물 조회", description = "게시물 목록 중 선택 시 해당 게시물 정보를 조회.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "조회성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "NEC", description = "NOT_EXISTING_CONTENTS", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "bIdx", description = "게시글 index", required = true, example = "1")
    })
    @GetMapping("/{bIdx}")
    public ResponseEntity<? super GetBoardResponseDto> getBoard(@PathVariable("bIdx") Integer bIdx) {
        ResponseEntity<? super GetBoardResponseDto> response = boardService.getBoard(bIdx);

        return response;
    }

    /**
     * 최신 게시물 전체 조회
     * @return
     */
    @Operation(summary = "최신 게시물 조회", description = "최신 게시물 목록 조회.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "조회성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/latest-list")
    public ResponseEntity<? super GetLatestBoardListResponseDto> getLatestBoardList() {
        ResponseEntity<? super GetLatestBoardListResponseDto> response = boardService.getLatestBoardList();
        return response;
    }

    /**
     * Paged list 조회
     * @param page
     * @param countPerPage
     * @return
     */
    @Operation(summary = "Paged 게시물 조회", description = "isPaged 파라미터 값이 true이면 페이징 처리한 결과 반환.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "조회성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "page", description = "현재 페이지", required = true, example = "1")
        ,@Parameter(name = "countPerPage", description = "페이지당 게시물 수", required = true, example = "3")
        ,@Parameter(name = "isPaged", description = "페이징 처리 여부", required = true, example = "true")
    })
    @GetMapping("/paged-list")
    public ResponseEntity<? super GetPaginationResponseDto> getPagedList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int countPerPage,
            @RequestParam(defaultValue = "true") boolean isPaged) {
        ResponseEntity<? super GetPaginationResponseDto> response = boardService.getPagedList(page, countPerPage, isPaged);
        return response;
    }


    /**
     * 게시물 등록
     * @param requestBody
     * @param email
     * @return
     */
    @Operation(summary = "게시물 등록", description = "관리자 게시물 등록. 인증 토큰을 헤더에 포함해야 함.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "등록성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "NU", description = "해당 ID 를 가진 관리자가 없음", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DBE", description = "DATABASE_ERROR", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(
            name = "Authorization",
            description = "Bearer token",
            required = true,
            in = ParameterIn.HEADER,
            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
        )
        ,@Parameter(name = "boardType", description = "PostBoardRequestDto 참조 게시글 type", required = true, example = "TP01")
        ,@Parameter(name = "publishDt", description = "게시 시작일", required = true, example = "2025-08-21 20:00:01")
        ,@Parameter(name = "title", description = "제목", required = true, example = "오디고 공지사항")
        ,@Parameter(name = "content", description = "내용", required = true, example = "안녕하세요. 오디고 어플 공지사항입니다. 이용자 여러분에게 전달해 줄 따끈한 소식이 있어요.")
        ,@Parameter(name = "boardImageList", description = "첨부 이미지파일", example = "http://localhost:8081/file/23ed2ee7-4cee-41e5-ae21-50d4dd23730c.jpg")
    })
    @PostMapping("")
    public ResponseEntity<? super PostBoardResponseDto> postBoardAdmin(
            @RequestBody @Valid PostBoardRequestDto requestBody,
            @AuthenticationPrincipal String id) {
            //@AuthenticationPrincipal String email) {

        ResponseEntity<? super PostBoardResponseDto> response = boardService.postBoardAdmin_my(requestBody, id);

        return response;
    }

    /**
     * 좋아요 등록
     * @param bIdx
     * @param email
     * @return
     */
    @PutMapping("/{bIdx}/favorite")
    public ResponseEntity<? super PutFavoriteResponseDto> putFavorite(
            @PathVariable("bIdx") Integer bIdx,
            @AuthenticationPrincipal String email) {

        ResponseEntity<? super PutFavoriteResponseDto> response = boardService.putFavorite(bIdx, email);
        return response;
    }

    /**
     * 등록된 좋아요 조회 List
     * 
     * @param bIdx
     * @return
     */
    @GetMapping("/{bIdx}/favorite-list")
    public ResponseEntity<? super GetFavoriteListResponseDto> getFavoriteList(
            @PathVariable("bIdx") Integer bIdx) {
        ResponseEntity<? super GetFavoriteListResponseDto> response = boardService.getFavoriteList(bIdx);

        return response;
    }

    /**
     * 댓글 목록 조회
     * 
     * @param bIdx
     * @return
     */
    @GetMapping("/{bIdx}/comment-list")
    public ResponseEntity<? super GetCommentListResponseDto> getCommentList(
            @PathVariable("bIdx") Integer bIdx) {
        ResponseEntity<? super GetCommentListResponseDto> response = boardService.getCommentList(bIdx);

        return response;
    }

    /**
     * 댓글 등록
     * 
     * @param requestBody
     * @param email
     * @param boardNum
     * @return
     */
    @PostMapping("/{bIdx}/comment")
    public ResponseEntity<? super PostCommentResponseDto> postComment(
            @RequestBody @Valid PostCommentRequestDto requestBody,
            @AuthenticationPrincipal String email,
            @PathVariable("bIdx") Integer boardNum) {

        ResponseEntity<? super PostCommentResponseDto> response = boardService.postComment(requestBody, boardNum,
                email);

        return response;
    }

    /**
     * 게시물 전체 가져오기 mybatis
     * @return
     */
    @GetMapping("/get_all")
    public ResponseEntity<CommonResponseDto<List<Map<String, Object>>>> getAllBoard() {
        CommonResponseDto<List<Map<String, Object>>> response = boardService.getAllBoard();
        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 1개 가져오기 mybatis
     * @param brdNum
     * @return
     */
    @GetMapping("/get_one/{bIdx}")
    public ResponseEntity<CommonResponseDto<Map<String, Object>>> getOneBoard(@PathVariable("bIdx") Integer brdIdx) {
        CommonResponseDto<Map<String, Object>> response = boardService.getOneBoard(brdIdx);
        return ResponseEntity.ok(response);
    }

}
