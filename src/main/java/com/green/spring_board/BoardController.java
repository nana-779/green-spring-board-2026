package com.green.spring_board;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board")
@AllArgsConstructor // 어노테이션 사용하고 생성자 삭제함 왜?

public class BoardController {
    private BoardRepository boardRepository;

//    //클래스의 프라이빗에 접근하려면? 게터, 세터나 생성자를 통해야함
//    //Controller가 Repository에 의존하고 있기 때문에 Repository를 전달받기 위한 생성자를 만듬
//    public BoardController(BoardRepository boardRepository) {
//        this.boardRepository = boardRepository;
//    }


    // 전체 조회
    @GetMapping
    public ResponseEntity<List<Boards>> getBoards(){
        return ResponseEntity.ok( // = 원하는 응답을 직접 셋팅하는걸 도와줌
                boardRepository.findAll()
        );
        // 스프링이 알아서 변수를 넣어줌
        // return boardRepository.findAll(); <- 데이터만 리턴
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Boards> getBoardDetail(@PathVariable int id) {
        Optional<Boards> optionalBoard = boardRepository.findById(id); // 안에 값이 있or없 박스를 받음
        if (optionalBoard.isEmpty()) {
            // 요청한 게시글 번호를 찾지 못한 경우
            return ResponseEntity.notFound().build();
        }
        // 게시글 꺼냄
        Boards board = optionalBoard.get();
        // 조회수 +1
        board.setHits(board.getHits() +1);
        // 조회수 +1된 게시글 저장
        boardRepository.save(board);
        // id가 있으면 게시글 반환
        return ResponseEntity.ok(board);
    }

    // 삽입(생성)
    @PostMapping
    public ResponseEntity<Boards> createBoard(@RequestBody BoardCreateRequest boardCreateRequest){
        if (boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Boards board = new Boards();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        Boards savedBoard = boardRepository.save(board);
        int newBoardId = savedBoard.getId();
        URI location = URI.create("/api/board/" + newBoardId);

        return ResponseEntity.created(location).body(savedBoard); // 응답이 없을땐 바디가 없어서 build를 붙여서 강제를 해야함?
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Boards> updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest) {

        Optional<Boards> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            // 요청한 게시글 번호를 찾지 못한 경우
            return ResponseEntity.notFound().build();
        }

        if (boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        // 위에거 다 거쳤으면 게시글을 꺼냄
        Boards board = optionalBoard.get();

        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        boardRepository.save(board);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id) {

        boolean isExist = boardRepository.existsById(id);

        if (!isExist) {
            return ResponseEntity.notFound().build();
        }
        boardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
