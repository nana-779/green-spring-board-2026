package com.green.spring_board.controller;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.DTO.BoardResponse;
import com.green.spring_board.DTO.BoardUpdateRequest;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<List<BoardResponse>> getBoards(){
        return ResponseEntity.ok( // = 원하는 응답을 직접 셋팅하는걸 도와줌
                boardService.getAllBoard()
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardDetail(@PathVariable int id) {

        try {
            BoardResponse board = boardService.getBoard(id);
            // 서비스에서 board를 못찾았으면 not found 반환
            if (board == null) {
                return ResponseEntity.notFound().build();
            }
            // id가 있으면 게시글 반환
            return ResponseEntity.ok(board);

        } catch (ResourceNotFountException e) {
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 삽입(생성)
    @PostMapping
    public ResponseEntity<Void> createBoard(@Valid @RequestBody BoardCreateRequest boardCreateRequest,
                                            HttpServletRequest httpServletRequest){

        try {
            // 세션 가져오기
            HttpSession session = httpServletRequest.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                return ResponseEntity.status(401).build();
            }
            // 세션에서 유저 아이디 뽑아오기
            int userId = (int) session.getAttribute("userId");

            int newBoardId = boardService.createBoard(boardCreateRequest, userId);
            URI location = URI.create("/api/board/" + newBoardId);

            return ResponseEntity.created(location).build();

        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable int id,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest) {

        try {
            boardService.updateBoard(id, boardUpdateRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFountException e) {
            return ResponseEntity.notFound().build();
        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id) {

        try {
            boardService.deleteBoard(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFountException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
