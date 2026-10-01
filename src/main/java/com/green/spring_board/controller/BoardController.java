package com.green.spring_board.controller;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.entity.Board;
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
    public ResponseEntity<List<Board>> getBoards(){
        return ResponseEntity.ok( // = 원하는 응답을 직접 셋팅하는걸 도와줌
                boardService.getAllBoard()
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardDetail(@PathVariable int id) {

        try {
            Board board = boardService.getBoard(id);
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
    public ResponseEntity<Void> createBoard(@RequestBody BoardCreateRequest boardCreateRequest){

        try {
            int newBoardId = boardService.createBoard(boardCreateRequest);
            URI location = URI.create("/api/board/" + newBoardId);

            return ResponseEntity.created(location).build();

        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Board> updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest) {

        try {
            boardService.updateBoard(id, boardCreateRequest);
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
