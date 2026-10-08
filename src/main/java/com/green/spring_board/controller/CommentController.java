package com.green.spring_board.controller;

import com.green.spring_board.DTO.ApiResponse;
import com.green.spring_board.DTO.CommentCreateRequest;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comments")

public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> createComment (
            @PathVariable int id,
            @Valid @RequestBody CommentCreateRequest commentCreateRequest,
            HttpServletRequest httpServletRequest) {

        // 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 세션에서 유저 아이디 뽑아오기
        int userId = (int) session.getAttribute("userId");

        commentService.createComments(id,userId, commentCreateRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
