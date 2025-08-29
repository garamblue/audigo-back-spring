package com.audigo.audigo_back.service.implement.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.audigo.audigo_back.dto.request.admin.board.PostBoardRequestDto;
import com.audigo.audigo_back.dto.request.admin.board.PostCommentRequestDto;
import com.audigo.audigo_back.dto.response.CommonResponseDto;
import com.audigo.audigo_back.dto.response.ResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetBoardResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetCommentListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetFavoriteListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetLatestBoardListResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.GetPaginationResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PostBoardResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PostCommentResponseDto;
import com.audigo.audigo_back.dto.response.admin.board.PutFavoriteResponseDto;
import com.audigo.audigo_back.entity.BoardEntity;
import com.audigo.audigo_back.entity.BoardListViewEntity;
import com.audigo.audigo_back.entity.CommentEntity;
import com.audigo.audigo_back.entity.FavoriteEntity;
import com.audigo.audigo_back.entity.ImageEntity;
import com.audigo.audigo_back.mapper.BoardMapper;
import com.audigo.audigo_back.repository.FavoriteRepository;
import com.audigo.audigo_back.repository.ImageRepository;
import com.audigo.audigo_back.repository.admin.AdminRepository;
import com.audigo.audigo_back.repository.admin.BoardListViewRepository;
import com.audigo.audigo_back.repository.admin.BoardRepository;
import com.audigo.audigo_back.repository.admin.CommentRepository;
import com.audigo.audigo_back.repository.resultSet.GetBoardResultSet;
import com.audigo.audigo_back.repository.resultSet.GetCommentListResultSet;
import com.audigo.audigo_back.repository.resultSet.GetFavoriteListResultSet;
import com.audigo.audigo_back.service.admin.BoardService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository; //jpa case
    private final BoardMapper boardMapper;         //mybatis case
    private final AdminRepository adminRepository;
    private final ImageRepository imgRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final BoardListViewRepository boardListViewRepository;

    /**
     * 게시글 등록 JPA / VO
     */
    @Override
    public ResponseEntity<? super PostBoardResponseDto> postBoardAdmin(PostBoardRequestDto dto, Integer aIdx) {
        try {
            boolean existed = adminRepository.existsByaIdx(aIdx);
            if (!existed)
                return PostBoardResponseDto.notExistedAdmin();

            BoardEntity boardEntity = new BoardEntity(dto, 0);
            // 1.게시물 내용 저장
            boardRepository.save(boardEntity);

            // 2.저장 후 auto 생성된 Board number 를 가져옴
            int bIdx = boardEntity.getBIdx();

            List<String> boardImageList = dto.getBoardImageList();
            List<ImageEntity> imageEntities = new ArrayList<>();

            for (String image : boardImageList) {
                ImageEntity imageEntity = new ImageEntity(bIdx, image);
                imageEntities.add(imageEntity);
            }
            // 3.게시물과 연결된 이미지들 저장
            imgRepository.saveAll(imageEntities);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PostBoardResponseDto.success();
    }

    /**
     * 게시글 등록 mybatis / VO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<? super PostBoardResponseDto> postBoardAdmin_my(PostBoardRequestDto dto, String id) {
        log.info("===== Admin id: " + id);
        boolean existedId = adminRepository.existsById(id);
        if (!existedId)
            return PostBoardResponseDto.notExistedAdmin();

        int aIdx = adminRepository.findAIdxById(id);
        BoardEntity boardEntity = new BoardEntity(dto, aIdx);
        // 1.게시물 내용 저장 후 자동 생성된 Board number 를 가져옴
        int resultRow = boardMapper.insertBoardAdmin(boardEntity);
        int genPK = boardEntity.getBIdx();

        log.info("===== postBoardAdmin resultRow: " + resultRow);
        log.info("===== postBoardAdmin genPK : " + genPK);

        if (resultRow < 1 )
            throw new RuntimeException("===== transaction 처리 중 예외 발생 : insertBoard =====");

        List<String> boardImageList = dto.getBoardImageList();
        List<ImageEntity> imageEntities = new ArrayList<>();

        //If boardImageList is not null
        if (boardImageList != null) {
            for (String image : boardImageList) {
                ImageEntity imageEntity = new ImageEntity(genPK, image);
                imageEntities.add(imageEntity);
            }
            // 2.게시물과 연결된 이미지들 저장
            imgRepository.saveAll(imageEntities);
        }

        return PostBoardResponseDto.success();
    }

    /**
     * 게시글 1개 조회
     */
    @Override
    public ResponseEntity<? super GetBoardResponseDto> getBoard(Integer bIdx) {
        log.info("===== getBoard called with bIdx: " + bIdx);
        GetBoardResultSet resultSet = null;
        List<ImageEntity> imageEntities = new ArrayList<>();

        try {
            resultSet = boardRepository.getBoard(bIdx);
            if (resultSet == null) {
                log.warn("===== getBoard resultSet is null for bIdx: " + bIdx);
                return GetBoardResponseDto.notExistingContents();
            }

            imageEntities = imgRepository.findBybIdx(bIdx);

            BoardEntity boardEntity = boardRepository.findBybIdx(bIdx);
            boardEntity.increaseViewCount();
            boardRepository.save(boardEntity);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetBoardResponseDto.success(resultSet, imageEntities);
    }

    /**
     * 좋아요 등록
     */
    @Override
    public ResponseEntity<? super PutFavoriteResponseDto> putFavorite(Integer bIdx, String email) {
        try {
            //boolean existedUser = userRepository.existsByEmail(email);
            //if (!existedUser)
            //    return PutFavoriteResponseDto.notExistedUser();

            BoardEntity boardEntity = boardRepository.findBybIdx(bIdx);
            if (boardEntity == null)
                return PutFavoriteResponseDto.notExistingContents();

            FavoriteEntity favoriteEntity = favoriteRepository.findBybIdxAndUserEmail(bIdx, email);
            if (favoriteEntity == null) {
                favoriteEntity = new FavoriteEntity(email, bIdx);
                favoriteRepository.save(favoriteEntity);
                boardEntity.increaseFavoriteCount();// 좋아요 증가
            } else {
                favoriteRepository.delete(favoriteEntity);
                boardEntity.decreaseFavoriteCount();// 좋아요 감소
            }

            boardRepository.save(boardEntity);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return PutFavoriteResponseDto.success();
    }

    /**
     * 좋아요 목록 조회
     */
    @Override
    public ResponseEntity<? super GetFavoriteListResponseDto> getFavoriteList(Integer bIdx) {
        // interface 객체 List
        List<GetFavoriteListResultSet> resultSets = new ArrayList<>();

        try {
            boolean existedBoard = boardRepository.existsBybIdx(bIdx);
            if (!existedBoard)
                return GetFavoriteListResponseDto.notExistingContents();

            resultSets = favoriteRepository.getFavoriteList(bIdx);
            if (resultSets == null)
                return GetFavoriteListResponseDto.notExistingContents();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetFavoriteListResponseDto.success(resultSets);
    }

    /**
     * 댓글 목록 조회
     */
    @Override
    public ResponseEntity<? super GetCommentListResponseDto> getCommentList(Integer bIdx) {

        // interface 객체 List 배열
        List<GetCommentListResultSet> resultSets = new ArrayList<>();

        try {
            boolean existedBoard = boardRepository.existsBybIdx(bIdx);
            if (!existedBoard)
                return GetCommentListResponseDto.notExistingContents();

            resultSets = commentRepository.getCommentList(bIdx);
            if (resultSets == null)
                return GetCommentListResponseDto.notExistingContents();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetCommentListResponseDto.success(resultSets);
    }

    /**
     * 댓글등록 후 success return
     */
    @Override
    public ResponseEntity<? super PostCommentResponseDto> postComment(
            PostCommentRequestDto dto, Integer boardNum, String email) {
        try {
            // 1)exception 처리
            BoardEntity boardEntity = boardRepository.findBybIdx(boardNum);
            if (boardEntity == null)
                return PostCommentResponseDto.notExistingContents();

            //boolean existedUser = userRepository.existsByEmail(email);
            //if (!existedUser)
            //    return PostCommentResponseDto.notExistedUser();

            // 2)comment 등록
            CommentEntity cmtEntity = new CommentEntity(dto, boardNum, email);
            commentRepository.save(cmtEntity);

            // 3)comment 수 증가
            boardEntity.increaseCommentCount();
            boardRepository.save(boardEntity);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }
        return PostCommentResponseDto.success();
    }

    /**
     * 게시판 정보 전체 가져오기
     */
    @Override
    public CommonResponseDto<List<Map<String, Object>>> getAllBoard() {
        List<Map<String, Object>> boards = new ArrayList<Map<String, Object>>();
        try {
            boards = boardMapper.selectAllBoard();
            if (boards == null)
                return CommonResponseDto.failure("ND", "No Data");
                
        } catch (Exception ex) {
            ex.printStackTrace();
            return CommonResponseDto.databaseError();
        }
        
        return CommonResponseDto.success(boards);
    }

    /**
     * 게시판 정보 하나 가져오기
     */
    @Override
    public CommonResponseDto<Map<String, Object>> getOneBoard(Integer boardNum) {
        Map<String, Object> board = new HashMap<String, Object>();
        try {
            board = boardMapper.selectOneBoard(boardNum);
            if (board == null)
                return CommonResponseDto.failure("ND", "No Data");
                
        } catch (Exception ex) {
            ex.printStackTrace();
            return CommonResponseDto.databaseError();
        }
        
        // @Param("articleId") String articleId
        //return CommonResponseDto.<Map<String, Object>>success(board); //명시적 지정1
        //CommonResponseDto<Map<String, Object>> result = CommonResponseDto.success(board);//명시적 지정2
        //return result; //명시적 지정2
        return CommonResponseDto.success(board); //자동으로 타입 추론
    }

    /**
     * 최신 게시글 목록 전체 조회 desc
     */
    @Override
    public ResponseEntity<? super GetLatestBoardListResponseDto> getLatestBoardList() {
        List<BoardListViewEntity> boardListViewEntities = new ArrayList<>(); 

        try {
            boardListViewEntities = boardListViewRepository.findAllByOrderByCdtDesc();
                
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetLatestBoardListResponseDto.success(boardListViewEntities);
    }

    /**
     * 페이징 처리한 게시글 목록 조회
     */
    @Override
    public ResponseEntity<? super GetPaginationResponseDto> getPagedList(int page, int countPerPage, boolean isPaged) {
        try {
            Page<BoardListViewEntity> boardList;
            if (isPaged) {
                Pageable pageable = PageRequest.of(page - 1, countPerPage);
                boardList = boardListViewRepository.findAllByOrderByCdtDesc(pageable);
            } else {
                List<BoardListViewEntity> boardListViewEntities = boardListViewRepository.findAllByOrderByCdtDesc();
                boardList = new PageImpl<>(boardListViewEntities);// 전체 데이터를 Page 객체로 변환
            }
            return GetPaginationResponseDto.success(boardList);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

}