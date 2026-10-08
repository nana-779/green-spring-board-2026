package com.green.spring_board.service;

import com.green.spring_board.DTO.CommentCreateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Comment;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.CommentRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
