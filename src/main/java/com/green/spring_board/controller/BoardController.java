package com.green.spring_board.controller;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.entity.Boards;
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
    public ResponseEntity<List<Boards>> getBoards(){
        return ResponseEntity.ok( // = 원하는 응답을 직접 셋팅하는걸 도와줌
                boardService.getAllBoard()
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Boards> getBoardDetail(@PathVariable int id) {
        Boards board = boardService.getBoard(id);
        // 서비스에서 board를 못찾았으면 not found 반환
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        // id가 있으면 게시글 반환
        return ResponseEntity.ok(board);
    }

    // 삽입(생성)
    @PostMapping
    public ResponseEntity<Void> createBoard(@RequestBody BoardCreateRequest boardCreateRequest){

        int newBoardId = boardService.createBoard(boardCreateRequest);
        if (newBoardId == -1) {
            return ResponseEntity.badRequest().build();
        }
        URI location = URI.create("/api/board/" + newBoardId);
        return ResponseEntity.created(location).build(); // 응답이 없을땐 바디가 없어서 build를 붙여서 강제를 해야함?
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Boards> updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest) {

        int code = boardService.updateBoard(id, boardCreateRequest);
        if (code == -1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id) {
        int code = boardService.deleteBoard(id);
        if (code == -1 ) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
