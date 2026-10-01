package com.green.spring_board.service;

import com.green.spring_board.DTO.SignupRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
}
