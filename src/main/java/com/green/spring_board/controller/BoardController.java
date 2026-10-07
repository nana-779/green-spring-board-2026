package com.green.spring_board.controller;

import com.green.spring_board.DTO.*;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// 전역 핸들러?

@RestController
@RequestMapping("/api/board")
@AllArgsConstructor // 어노테이션 사용하고 생성자 삭제함 왜?

public class BoardController {

    private final BoardService boardService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoards(){
        return ResponseEntity.ok( // = 원하는 응답을 직접 셋팅하는걸 도와줌
                ApiResponse.ok(boardService.getAllBoard())
        );
    }

    // 내 게시글 조회
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoard(HttpServletRequest httpServletRequest) {
        // 1. 세션 확인
        // 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        // 2. 유저 id 확인
        int userId = (int) session.getAttribute("userId");

        return ResponseEntity.ok(
                ApiResponse.ok(boardService.getMyBoard(userId))
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardDetail(@PathVariable int id) {

            BoardResponse board = boardService.getBoard(id);

            // id가 있으면 게시글 반환
            return ResponseEntity.ok(ApiResponse.ok(board));
    }

    // 삽입(생성)
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(@Valid @RequestBody BoardCreateRequest boardCreateRequest,
                                            HttpServletRequest httpServletRequest){

            // 세션 가져오기
            HttpSession session = httpServletRequest.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new UnauthenticatedException("로그인이 필요합니다");
            }

            // 세션에서 유저 아이디 뽑아오기
            int userId = (int) session.getAttribute("userId");
            int newBoardId = boardService.createBoard(boardCreateRequest, userId);
            URI location = URI.create("/api/board/" + newBoardId);

            return ResponseEntity
                    .created(location)
                    .body(ApiResponse.ok());
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(
            @PathVariable int id,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest,
            HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int userId = (int) session.getAttribute("userId");

        boardService.updateBoard(id, boardUpdateRequest, userId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable int id,
                                            HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int userId = (int) session.getAttribute("userId");

        boardService.deleteBoard(id, userId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 좋아요를 누르면 DB에 행이 추가되니까 Post
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard (@PathVariable int id, HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int userId = (int) session.getAttribute("userId");

        boardService.pressLike(id, userId);
        return ResponseEntity.ok(ApiResponse.ok());

        // 다시 눌렀을때 취소 O
        // 좋아요 수 O
        // 상세 눌렀을때 어느 유저가 눌렀는지

        // 내가 이 게시글에 좋아요 눌렀는지
    }
}
