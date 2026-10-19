package com.green.spring_board.exceptions;

// 요청한 작업을 수행하기에 현재 객체의 상태가 올바르지 않을때
// 앞서 처리된 작업을 또 처리하거나, 순서가 꼬였을때
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
