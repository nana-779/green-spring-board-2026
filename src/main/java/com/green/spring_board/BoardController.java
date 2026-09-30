package com.green.spring_board;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    private BoardRepository boardRepository;

    //클래스의 프라이빗에 접근하려면? 게터, 세터나 생성자를 통해야함
    //Controller가 Repository에 의존하고 있기 때문에 Repository를 전달받기 위한 생성자를 만듬
    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 전체 조회
    @GetMapping
    public List<Boards> getBoards(){
        // 스프링이 알아서 변수를 넣어줌
        return boardRepository.findAll();
    }

    // 상세 조회
    @GetMapping("/{id}")
    public Boards getBoardDetail(@PathVariable int id) {
        return boardRepository.findById(id).get();
    }

    // 삽입(생성)
    @PostMapping
    public void createBoard(@RequestBody BoardCreateRequest boardCreateRequest){
        System.out.println(boardCreateRequest.getTitle());
        System.out.println(boardCreateRequest.getContent());

        Boards boards = new Boards();
        boards.setTitle(boardCreateRequest.getTitle());
        boards.setContent(boardCreateRequest.getContent());

        boardRepository.save(boards);
    }

    // 수정
    @PatchMapping("/{id}")
    public void updateBoard(
            @PathVariable int id,
            @RequestBody BoardCreateRequest boardCreateRequest) {

        Boards boards = boardRepository.findById(id).get();

        if (boardCreateRequest.getTitle() != null) {
            boards.setTitle(boardCreateRequest.getTitle());
        }
        if (boardCreateRequest.getContent() != null) {
            boards.setContent(boardCreateRequest.getContent());
        }
        boardRepository.save(boards);
    }


    // 삭제
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable int id) {
        boardRepository.deleteById(id);
    }
}
