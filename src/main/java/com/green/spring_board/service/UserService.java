package com.green.spring_board.service;

import com.green.spring_board.DTO.LoginRequest;
import com.green.spring_board.DTO.MyInfoResponse;
import com.green.spring_board.DTO.SignupRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    public void signup (SignupRequest signupRequest) {
        // 유저네임, 비밀번호가 공백인지 아닌지 확인
        if (signupRequest.getEmail().isBlank() || signupRequest.getPassword().isBlank()) {
            throw new UserRequestException("이메일과 비밀번호는 공백일 수 없습니다");
        }
        // 이메일이 사용중인지 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceConflictException("이메일이 이미 사용중입니다");
        }

        // password 해싱
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // db save
        User user = new User();
        user.setNickname(signupRequest.getNickname());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    // 로그인
    public int login (LoginRequest loginRequest) {
        // 1. 이메일이 존재하는지 확인
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }
        User user = optionalUser.get();
        // 2. 비밀번호가 올바른지 확인
        // matches = 사용자가 입력한 비밀번호를 알아서 해싱해서 내가 DB에 갖고있는 해싱된 비밀번호와 대조함
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthenticatedException("비밀번호가 틀립니다");
        }
        // 3. 로그인 성공 처리
        return user.getId();
    }

    // 로그인 한 유저 정보 가져오기
    public MyInfoResponse getUserInfo(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }
        User user = optionalUser.get();
        // 3. 유저 아이디로 DB 조회
        // 4. DB에서 유저의 닉네임과 이메일 받아오기
        String email = user.getEmail();
        String nickname = user.getNickname();
        // 5. 반환
        // DTO를 객체생성해서 리턴?
        MyInfoResponse myInfoResponse = new MyInfoResponse();
        myInfoResponse.setEmail(email);
        myInfoResponse.setNickname(nickname);
        return myInfoResponse;
    }

    // 유저 정보 수정
    public void updateUserInfo (int userId, MyInfoResponse myInfoResponse) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }

        User user = optionalUser.get();

        // null이면 수정하지 않기
        if (myInfoResponse.getEmail() == null || myInfoResponse.getEmail().isBlank()) {
            throw new UserRequestException("잘못된 입력값");
        } user.setEmail(myInfoResponse.getEmail());

        if (myInfoResponse.getNickname() == null || myInfoResponse.getNickname().isBlank()) {
            throw new UserRequestException("잘못된 입력값");
        } user.setNickname(myInfoResponse.getNickname());

        userRepository.save(user);
    }

    // 유저 삭제
    public void deleteUser (int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }

        User user = optionalUser.get();
        userRepository.delete(user);
    }
}
