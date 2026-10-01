package com.green.spring_board.service;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    public List<Board> getAllBoard () {
        return boardRepository.findAll();
    }

    public Board getBoard (int id){
        Optional<Board> optionalBoard = boardRepository.findById(id); // 안에 값이 있or없 박스를 받음
        if (optionalBoard.isEmpty()) {
            // 요청한 게시글 번호를 찾지 못한 경우
            throw new ResourceNotFountException("요청한 게시글을 찾지 못했습니다");
        }
        // 게시글 꺼냄
        Board board = optionalBoard.get();

        // 조회수 +1
        board.setHits(board.getHits() +1);
        // 조회수 +1된 게시글 저장
        boardRepository.save(board);
        // board 반환
        return board;
    }

    public int createBoard(BoardCreateRequest boardCreateRequest) {
         if (boardCreateRequest.getTitle() == null || boardCreateRequest.getTitle().isBlank()) {
             throw new UserRequestException("잘못된 입력값입니다");
         }
         if (boardCreateRequest.getContent() == null || boardCreateRequest.getContent().isBlank()) {
             throw new UserRequestException("잘못된 입력값입니다");
         }

         Board board = new Board();
         board.setTitle(boardCreateRequest.getTitle());
         board.setContent(boardCreateRequest.getContent());

         Board savedBoard = boardRepository.save(board);

         return savedBoard.getId();
    }

    public void updateBoard (int id, BoardCreateRequest boardCreateRequest) {
         Optional<Board> optionalBoard = boardRepository.findById(id);

         if (optionalBoard.isEmpty()) {
             // 요청한 게시글 번호를 찾지 못한 경우
             throw new ResourceNotFountException("게시글을 찾을 수 없습니다");
         }
         // 위에거 다 거쳤으면 게시글을 꺼냄
         Board board = optionalBoard.get();

         if (boardCreateRequest.getTitle() != null && !boardCreateRequest.getTitle().isBlank()) {
             board.setTitle(boardCreateRequest.getTitle());
         }
         if (boardCreateRequest.getContent() != null && !boardCreateRequest.getContent().isBlank()) {
             board.setContent(boardCreateRequest.getContent());
         }

         boardRepository.save(board);
    }

    public void deleteBoard(int id) {
        boolean isExist = boardRepository.existsById(id);

        if (!isExist) {
            throw new ResourceNotFountException("게시글을 찾을 수 없습니다");
        }
        boardRepository.deleteById(id);
    }
}
