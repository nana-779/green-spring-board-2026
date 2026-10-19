package com.green.spring_board.global;

import com.green.spring_board.DTO.ApiResponse;
import com.green.spring_board.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

// 전역으로 handler를 처리하기위해 붙이는 어노테이션
@RestControllerAdvice
// 로그를 남기기 위한 어노테이션
@Slf4j
public class GlobalExceptionHandler {

    // 요청한 데이터가 없을때
    @ExceptionHandler (ResourceNotFountException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFountException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 로그인 정보가 없거나 적절하지 않을때
    // 로그인하지 않은 사용자가 글을 삭제하거나 수정하려할때 등
    @ExceptionHandler (UnauthenticatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthenticated(UnauthenticatedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // Validator가 입력 검증 과정에서 에러를 발견했을때
    // 뭐때문에 실패했는지 알려주기위해 resultMessage 출력
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationError(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String resultMessage = "";
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        for (FieldError error : errors) {
            resultMessage = resultMessage + error.getField() + "은(는)" +
                    error.getDefaultMessage() + "\n";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(resultMessage));
    }

    // 중복때문에 데이터베이스에 저장 실패 시
    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataConflict(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("중복되거나 저장할 수 없는 데이터입니다."));
    }

    // 누구인지 인증은 되어있지만 해당 작업을 허용하지 않은 경우 (403)
    @ExceptionHandler (AuthorizationFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden (AuthorizationFailureException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 요청한 작업을 수행하기에 현재 객체의 상태가 올바르지 않을때
    // 앞서 처리된 작업을 또 처리하거나, 순서가 꼬였을때
    @ExceptionHandler (InvalidStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(InvalidStateException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 이메일이 사용중이거나 중복일때
    @ExceptionHandler (ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ResourceConflictException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 나머지 처리 500으로
    @ExceptionHandler (Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버에서 오류가 발생했습니다."));
    }
}
