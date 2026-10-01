package com.green.spring_board.controller;

import com.green.spring_board.DTO.SignupRequest;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.channels.ReadPendingException;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor

public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<Void> singup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.signup(signupRequest);
            return ResponseEntity.ok().build();

        } catch (ResourceConflictException e) {
            // 충돌때문에 데이터가 들어갈 수 없을때
            // 직접 status로 status code를 설정해주면 됨
            return ResponseEntity.status(409).build();

        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //

}
