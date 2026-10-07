package com.green.spring_board.service;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.DTO.BoardResponse;
import com.green.spring_board.DTO.BoardUpdateRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    public List<BoardResponse> getAllBoard () {
        List<Board> boards = boardRepository.findAll();
        List<BoardResponse> boardResponse = new ArrayList<>();

        for (Board board : boards) {
            boardResponse.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }
        return boardResponse;
    }

    // List<Board> -> List<BoardResponse> 로 변환
    // 1. 리스트 보드 리스폰스 형태의 빈 리스트 생성
    // 2. 보드 개수만큼 반복하여 new boardresponse 생성
    // 3. 1번에서 만든 리스트에 추가


    public BoardResponse getBoard (int id){
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
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getCreatedDatetime(),
                board.getUpdatedDatetime()
        );
    }

    public int createBoard(BoardCreateRequest boardCreateRequest, Integer userId) {

        //유저 객체 만들고 유효성 체크
        // TODO :: 이후 삭제/탈퇴 유저에 대한 검증도 필요
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }

        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        board.setUser(user.get());

        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    public void updateBoard (int id, BoardUpdateRequest boardUpdateRequest, int userId) {
        // 작성자와 요청자가 다르다면 DB에 다녀올필요 X
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            // 요청한 게시글 번호를 찾지 못한 경우
            throw new ResourceNotFountException("게시글을 찾을 수 없습니다");
        }
        // 위에거 다 거쳤으면 게시글을 꺼냄
        Board board = optionalBoard.get();

        // 이 경우엔 보드에 접근해서 작성자를 알아야해서 DB접근 후에 코드 추가
        // 요청자의 userId 확인
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException("해당 게시글의 작업 권한이 없습니다");
        }

        if (boardUpdateRequest.getTitle() != null && !boardUpdateRequest.getTitle().isBlank()) {
            board.setTitle(boardUpdateRequest.getTitle());
        }

        if (boardUpdateRequest.getContent() != null && !boardUpdateRequest.getContent().isBlank()) {
            board.setContent(boardUpdateRequest.getContent());
        }

        boardRepository.save(board);
    }

    public void deleteBoard(int id, int userId) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFountException("게시글을 찾을 수 없습니다");
        }

        // 위에거 다 거쳤으면 게시글을 꺼냄
        Board board = optionalBoard.get();

        // 이 경우엔 보드에 접근해서 작성자를 알아야해서 DB접근 후에 코드 추가
        // 요청자의 userId 확인
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException("해당 게시글의 작업 권한이 없습니다");
        }
        boardRepository.deleteById(id);
    }
}
