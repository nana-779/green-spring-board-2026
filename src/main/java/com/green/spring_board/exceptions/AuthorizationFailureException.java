package com.green.spring_board.exceptions;

// 누구인지 인증은 되어있지만 해당 작업을 허용하지 않은 경우 (403)
// 예시: 자신이 작성하지 않은 게시글 삭제나 회원 삭제 방지
public class AuthorizationFailureException extends RuntimeException {
    public AuthorizationFailureException(String message) {
        super(message);
    }
}
