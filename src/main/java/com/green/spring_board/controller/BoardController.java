package com.green.spring_board.controller;

import com.green.spring_board.DTO.*;
import com.green.spring_board.service.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/board")
@AllArgsConstructor // 어노테이션 사용하고 생성자 삭제함 왜?

public class BoardController {
    private final BoardService boardService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponse>>> getBoards(
            @RequestParam(defaultValue = "0") int page, // 지금 내가 몇번째 페이지 보고이슨지
            @RequestParam(defaultValue = "10") int size, // 한 페이지에 몇개 볼건지
            @RequestParam(defaultValue = "latest") String order)
    {
        return ResponseEntity.ok( // = 원하는 응답을 직접 셋팅하는걸 도와줌
                ApiResponse.ok(boardService.getAllBoard(page, size, order))
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardDetail(
            @PathVariable int id)
    {
        BoardResponse board = boardService.getBoard(id);
        // id가 있으면 게시글 반환
        return ResponseEntity.ok(ApiResponse.ok(board));
    }

    // 내 게시글 조회
    @GetMapping("/my-boards")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoard()
    {
        return ResponseEntity.ok(
                ApiResponse.ok(boardService.getMyBoard())
        );
    }

    // 삽입(생성)
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest
    )
    {


            int newBoardId = boardService.createBoard(boardCreateRequest);
            URI location = URI.create("/api/board/" + newBoardId);

            return ResponseEntity
                    .created(location)
                    .body(ApiResponse.ok());
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(
            @PathVariable int id,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest)
    {
        boardService.updateBoard(id, boardUpdateRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable int id)
    {
        boardService.deleteBoard(id);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 좋아요 누르기, 취소
    // 좋아요를 누르면 DB에 행이 추가되니까 Post
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard (@PathVariable int id)
    {
        boardService.pressLike(id);
        return ResponseEntity.ok(ApiResponse.ok());

        // 다시 눌렀을때 취소 O
        // 좋아요 수 O
    }

    // 상세 눌렀을때 어느 유저가 눌렀는지
    @GetMapping ("/like/{id}")
    public ResponseEntity<ApiResponse<LikeDetailResponse>> viewLikeDatils(@PathVariable int id)
    {
        // 이 게시글에 좋아요 누른 유저들의 유저명
        LikeDetailResponse response = boardService.getLikeDetail(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
