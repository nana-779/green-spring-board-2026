package com.green.spring_board.service;

import com.green.spring_board.DTO.BoardCreateRequest;
import com.green.spring_board.DTO.BoardResponse;
import com.green.spring_board.DTO.BoardUpdateRequest;
import com.green.spring_board.DTO.LikeDetailResponse;
import com.green.spring_board.entity.Like;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.InvalidStateException;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.LikeRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private LikeRepository likeRepository;

    public Page<BoardResponse> getAllBoard (int userId, int page, int size, String order) {
        Sort sort;
        if (order.equals("latest")) {
            sort = Sort.by(Sort.Direction.DESC, "createdDatetime");
        } else if (order.equals("likes")) {
            sort = Sort.by(Sort.Direction.DESC, "likeCount");
        } else if (order.equals("views")) {
            sort = Sort.by(Sort.Direction.DESC, "hits");
        } else {
            throw new InvalidStateException("잘못된 정렬 옵션입니다");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Board> boards = boardRepository.findByIsDeletedFalse(pageable);

        List<BoardResponse> boardResponse = new ArrayList<>();

        for (Board board : boards) {
            boardResponse.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getLikeCount(),
                            (userId == -1) ? false : likeRepository.existsByUserIdAndBoardId(userId, board.getId()),
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }
        return new PageImpl<>(boardResponse, pageable, boards.getTotalElements());
    }
    // List<Board> -> List<BoardResponse> 로 변환
    // 1. 리스트 보드 리스폰스 형태의 빈 리스트 생성
    // 2. 보드 개수만큼 반복하여 new boardresponse 생성
    // 3. 1번에서 만든 리스트에 추가


    public BoardResponse getBoard (int id, int userId){
        Optional<Board> optionalBoard = boardRepository.findById(id); // 안에 값이 있or없 박스를 받음
        if (optionalBoard.isEmpty()) {
            // 요청한 게시글 번호를 찾지 못한 경우
            throw new ResourceNotFountException("요청한 게시글을 찾지 못했습니다");
        }
        // 게시글 꺼냄
        Board board = optionalBoard.get();

        if (board.isDeleted()) {
            throw new ResourceNotFountException("삭제된 게시글입니다");
        }
        // 조회수 +1
        board.setHits(board.getHits() + 1);
        // 조회수 +1된 게시글 저장
        boardRepository.save(board);
        // board 반환
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                board.getLikeCount(),
                (userId == -1) ? false : likeRepository.existsByUserIdAndBoardId(userId, board.getId()),
                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getCreatedDatetime(),
                board.getUpdatedDatetime()
        );
    }

    // 내 게시글 조회
    public List<BoardResponse> getMyBoard (int userId) {
        List<Board> boards = boardRepository.findByUserIdAndIsDeletedFalse(userId);
        List<BoardResponse> myBoardResponse = new ArrayList<>();

        // 게시글이 없으면 없다고 알리기
        if (boards.isEmpty()) {
            throw new ResourceNotFountException("게시글을 찾지 못했습니다");
        }

        for (Board board : boards) {
            myBoardResponse.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getLikeCount(),
                            likeRepository.existsByUserIdAndBoardId(userId, board.getId()),
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }
        return myBoardResponse;
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

        if (board.isDeleted()) {
            throw new ResourceNotFountException("삭제된 게시글입니다");
        }

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

        board.setDeleted(true);
        boardRepository.save(board);
    }

    public void pressLike (int id, int userId) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFountException("존재하지 않는 게시글입니다");
        } Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("존재하지 않는 유저입니다");
        } User user = optionalUser.get();

        // 1. 이 유저와 보드로 동일한 좋아요가 있느지 확인
        Optional<Like> optionalLike = likeRepository.findByUserIdAndBoardId(userId, id);

        // 없으면 -> 추가
        if (optionalLike.isEmpty()) {
            Like like = new Like();
            like.setUser(user);
            like.setBoard(board);
            likeRepository.save(like);

            // 좋아요 수 카운트 +
            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);
        } else {
            // 있으면 -> 삭제
            Like like = optionalLike.get();
            likeRepository.deleteById(like.getId());

            // 좋아요 수 카운트 -
            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);
        }
    }

    public LikeDetailResponse getLikeDetail (int id) {
        // 이 게시글에 좋아요 누른 유저 정보들을 likes 테이블에서 싹 가져오기
        List<Like> likes = likeRepository.findByBoardId(id);
        // 그 유저들의 닉네임을 뽑아서 LikeDetailResponse에 넣기
        LikeDetailResponse likeDetailResponse = new LikeDetailResponse();
        List<String> nicknames = new ArrayList<>();

        for (Like like : likes) {
            nicknames.add(like.getUser().getNickname());
        }
        likeDetailResponse.setLikedUserNames(nicknames);
        // LikeDetailResponse 반환
        return likeDetailResponse;
    }
}
