package com.green.spring_board.service;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.entity.Boards;
import com.green.spring_board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    public List<Boards> getAllBoard () {
        return boardRepository.findAll();
    }

    public Boards getBoard (int id) {
        Optional<Boards> optionalBoard = boardRepository.findById(id); // 안에 값이 있or없 박스를 받음
        if (optionalBoard.isEmpty()) {
            // 요청한 게시글 번호를 찾지 못한 경우
            return null; // 보드 대신 null 반환해서
        }
        // 게시글 꺼냄
        Boards board = optionalBoard.get();
        // 조회수 +1
        board.setHits(board.getHits() +1);
        // 조회수 +1된 게시글 저장
        boardRepository.save(board);
        // board 반환
        return board;
    }

    public int createBoard(BoardCreateRequest boardCreateRequest) {
         if (boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
             return -1;
         }
         if (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
             return -1;
         }

         Boards board = new Boards();
         board.setTitle(boardCreateRequest.getTitle());
         board.setContent(boardCreateRequest.getContent());

         Boards savedBoard = boardRepository.save(board);

         return savedBoard.getId();
    }

    public int updateBoard (int id, BoardCreateRequest boardCreateRequest) {
         Optional<Boards> optionalBoard = boardRepository.findById(id);

         if (optionalBoard.isEmpty()) {
             // 요청한 게시글 번호를 찾지 못한 경우
             return -1;
         }
         // 위에거 다 거쳤으면 게시글을 꺼냄

         Boards board = optionalBoard.get();

         if (boardCreateRequest.getTitle() != null && !boardCreateRequest.getTitle().isBlank()) {
             board.setTitle(boardCreateRequest.getTitle());
         }
         if (boardCreateRequest.getContent() != null && !boardCreateRequest.getContent().isBlank()) {
             board.setContent(boardCreateRequest.getContent());
         }

         boardRepository.save(board);
         return 0;
    }

    public int deleteBoard(int id) {
        boolean isExist = boardRepository.existsById(id);

        if (!isExist) {
            return -1;
        }
        boardRepository.deleteById(id);
        return 0;
    }
}
