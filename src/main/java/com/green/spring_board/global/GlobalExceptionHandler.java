package com.green.spring_board.global;

import com.green.spring_board.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// 전역으로 handler를 처리하기위해 붙이는 어노테이션
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 요청한 데이터가 없을때
    @ExceptionHandler (ResourceNotFountException.class)
    public ResponseEntity<Void> handleNotFound(ResourceNotFountException e) {
        return ResponseEntity.notFound().build();
    }

    // 로그인 정보가 없거나 적절하지 않을때
    // 로그인하지 않은 사용자가 글을 삭제하거나 수정하려할때 등
    @ExceptionHandler (UnauthenticatedException.class)
    public ResponseEntity<Void> handleUnauthenticated(UnauthenticatedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Validator가 입력 검증 과정에서 에러를 발견했을때
    // 뭐때문에 실패했는지 알려주기위해 resultMessage 출력
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException e) {
        String resultMessage = "";
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        for (FieldError error : errors) {
            resultMessage = resultMessage + error.getField() + "은(는)" +
                    error.getDefaultMessage() + "\n";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body(resultMessage);
    }

    // 중복때문에 데이터베이스에 저장 실패 시
    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataConflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body("중복되거나 저장할 수 없는 데이터입니다.");
    }

    // 누구인지 인증은 되어있지만 해당 작업을 허용하지 않은 경우 (403)
    @ExceptionHandler (AuthorizationFailureException.class)
    public ResponseEntity<Void> handleForbidden (AuthorizationFailureException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 요청한 작업을 수행하기에 현재 객체의 상태가 올바르지 않을때
    // 앞서 처리된 작업을 또 처리하거나, 순서가 꼬였을때
    @ExceptionHandler (InvalidStateException.class)
    public ResponseEntity<Void> handleBadRequest(InvalidStateException e) {
        return ResponseEntity.badRequest().build();
    }

    // 이메일이 사용중이거나 중복일때
    @ExceptionHandler (ResourceConflictException.class)
    public ResponseEntity<String> handleConflict(ResourceConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // 나머지 처리 500으로
    @ExceptionHandler (Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body("서버에서 오류가 발생했습니다.");
    }
}
