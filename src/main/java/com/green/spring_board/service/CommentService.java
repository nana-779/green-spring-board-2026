package com.green.spring_board.service;

import com.green.spring_board.DTO.CommentCreateRequest;
import com.green.spring_board.DTO.CommentResponse;
import com.green.spring_board.DTO.CommentUpdateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Comment;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.CommentRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepository;
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    public void createComments (int id,
                                int userId,
                                CommentCreateRequest commentCreateRequest){
        //어떤 게시글에 다는지 확인(보드id가 있는지 확인하고 있으면 객체로 받기)
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFountException("존재하지 않는 게시글입니다");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }
        User user = optionalUser.get();

        //코멘트 객체 만들어서 유저 id, 보드id, 내용 받기?
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setComment(commentCreateRequest.getContent());

        // DB에 저장
        commentRepository.save(comment);
    }

    public List<CommentResponse> readComments(int boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ResourceNotFountException("게시글을 찾을 수 없습니다");
        }
        // 게시글을 기반ㅇ로 댓글을 조회
        List<Comment> comments = commentRepository.findByBoardId(boardId);

        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse = new CommentResponse();

            commentResponse.setCommentId(comment.getId());
            commentResponse.setContent(comment.getComment());
            commentResponse.setNickname(comment.getUser().getNickname());
            commentResponse.setCommentDate(comment.getCreatedDatetime());

            commentResponses.add(commentResponse);
        }
        return commentResponses;
    }

    public void updateComment (int id, int userId, CommentUpdateRequest commentUpdateRequest) {
        // 댓글이 존재하지 않는 경우
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new ResourceNotFountException("댓글이 존재하지 않습니다");
        }
        Comment comment = optionalComment.get();

        // 작성자가 권한이 있는지 확인
        if (comment.getUser().getId() != userId) {
            throw new AuthorizationFailureException("해당 작업 권한이 없습니다");
        }

        //if commentupdaterequest가 null, blank가 아니면 comment.setContent(commentupdateRequest.getContent()
        if (commentUpdateRequest.getContent() != null && !commentUpdateRequest.getContent().isBlank()) {
            comment.setComment(commentUpdateRequest.getContent());
        }
        //그리고 저장
        commentRepository.save(comment);
    }

    public void deleteComment (int id, int userId) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new ResourceNotFountException("댓글을 찾을 수 없습니다");
        }
        // 위에거 다 거쳤으면 게시글을 꺼냄
        Comment comment = optionalComment.get();

        // 이 경우엔 보드에 접근해서 작성자를 알아야해서 DB접근 후에 코드 추가
        // 요청자의 userId 확인
        if (comment.getUser().getId() != userId) {
            throw new AuthorizationFailureException("해당 작업 권한이 없습니다");
        }
        commentRepository.delete(comment);
    }
}
