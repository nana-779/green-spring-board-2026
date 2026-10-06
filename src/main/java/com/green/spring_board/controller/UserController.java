package com.green.spring_board.controller;

import com.green.spring_board.DTO.LoginRequest;
import com.green.spring_board.DTO.MyInfoResponse;
import com.green.spring_board.DTO.SignupRequest;
import com.green.spring_board.DTO.UserUpdateRequest;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.boot.tomcat.autoconfigure.TomcatServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor

public class UserController {
    private final UserService userService;
    private final TomcatServerProperties tomcatServerProperties;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> singup(@Valid @RequestBody SignupRequest signupRequest) {
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

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest,
                                      HttpServletRequest httpServletRequest) {

        // DTO Valid (
        try {
            int userId = userService.login(loginRequest);
            // 세션작업
            HttpSession session = httpServletRequest.getSession();
            httpServletRequest.changeSessionId();
            session.setAttribute("userId", userId);

            return ResponseEntity.ok().build();
            // 스프링부트랑 톰캣이 관리함, 클라이언트의 요청이 세션정보를 갖고있으면 개발자에게 전달, 아니면
        } catch (ResourceNotFountException e) {
            return ResponseEntity.notFound().build();

        } catch (UnauthenticatedException e) {
            return ResponseEntity.status(401).build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // "내" 정보 조회 (이메일과 닉네임만, 비밀번호는 이미 해싱해버려서 반환 불가)
    // session은 비즈니스 로직이 아니고 패킷 관련?이라서 controller에서 하는게 나음
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> getCurrentUser(HttpServletRequest httpServletRequest) {
        // 1. 이 사람의 세션을 가져옴
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }
        // 2. 세션에서 유저아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        MyInfoResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok().body(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }
        // 다음부터 못사용하게 일련번호를 만료처리해서 다음에 재사용못하게
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    // 회원 수정
    @PatchMapping("/update")
    public ResponseEntity<Void> updateUserInfo (HttpServletRequest request,
                                                @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        // 이메일, 닉네임 업데이트

        // 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            // 현재 유저를 가져와서, 해당 유저 정보를 사용자가 올린 요청으로 덮어씌운다
            int userId = (int) session.getAttribute("userId");

            //(서비스로옮긴걸 호출해와야함)
            // UserService의 updateUserInfo 메서드에서
            // 세션의 userId랑 내가 requestBody로 받은 response를 가져와야한다
            userService.updateUserInfo(userId, userUpdateRequest);

            return ResponseEntity.ok().build();

        } catch (ResourceNotFountException e) {
            return ResponseEntity.notFound().build();

        } catch (UserRequestException e) {
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 회원 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        // 정보 받지않고 바로 삭제 (서블릿리퀘스트로 리퀘스트는 받아야함)
        // 로그인이 되어있는지 세션부터 확인해야함
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }

        int userId = (int) session.getAttribute("userId");

        // 1. DB 삭제
        // 나중을 대비해서 삭제로직은 서비스로 옮김F
        // 서비스의 deleteUser 메서드가 해줌
        userService.deleteUser(userId);
        // 2. 로그아웃(세션아웃)
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
