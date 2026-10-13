package com.green.spring_board.service;

import com.green.spring_board.DTO.*;
import com.green.spring_board.entity.RefreshToken;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.AuthorizationFailureException;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFountException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.RefreshTokenRepository;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public void signup (SignupRequest signupRequest) {

        // username이 사용중인지 확인
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new ResourceConflictException("유저네임이 이미 사용중입니다");
        }

        // 이메일이 사용중인지 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceConflictException("이메일이 이미 사용중입니다");
        }

        // password 해싱
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // db save
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setNickname(signupRequest.getNickname());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setState(UserState.ACTIVE);
        userRepository.save(user);
    }

    // 로그인
    public LoginResponse login (LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            ));
        } catch (AuthenticationException e) {
            throw new UnauthenticatedException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        // 1. 이메일이 존재하는지 확인
        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user == null) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }

        if (user.getState() == UserState.QUITTED) {
            throw new ResourceNotFountException("탈퇴한 회원입니다");
        }

        // 2. 액세스, 리프레시 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // 해당 유저의 기존 리프레시 토큰 삭제
        // refreshTokenRepository.deleteByUserId(user.getId()); // < 이거 사용하려면 @Transactional 어노테이션 써야함
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(user.getId());
        refreshTokenRepository.deleteAll(tokens);

        // 해당 유저의 신규 리프레시 토큰 저장
        Date expiration = jwtTokenProvider.getExpiration(refreshToken);
        LocalDateTime expirationDateTime = expiration.toInstant().atZone(
                ZoneId.systemDefault()
        ).toLocalDateTime();

        RefreshToken refresh = new RefreshToken();
        refresh.setUser(user);
        refresh.setToken(refreshToken);
        refresh.setExpirationDatetime(expirationDateTime);
        refreshTokenRepository.save(refresh);
        // 3. 로그인 성공 처리
        return new LoginResponse(accessToken, refreshToken);
    }

    // 로그인 한 유저 정보 가져오기
    public MyInfoResponse getUserInfo(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }
        User user = optionalUser.get();

        if (user.getState() == UserState.QUITTED) {
            throw new ResourceNotFountException("탈퇴한 회원입니다");
        }

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
    public void updateUserInfo (int userId, UserUpdateRequest userUpdateRequest) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }

        User user = optionalUser.get();

        if (user.getState() == UserState.QUITTED) {
            throw new ResourceNotFountException("탈퇴한 회원입니다");
        }

        // null이면 수정하지 않기
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isBlank()) {
            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getNickname() != null && !userUpdateRequest.getNickname().isBlank()) {
            user.setNickname(userUpdateRequest.getNickname());
        }

        userRepository.save(user);
    }

    // 유저 삭제
    public void deleteUser (int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFountException("유저를 찾을 수 없습니다");
        }
        User user = optionalUser.get();

        user.setState(UserState.QUITTED);
        userRepository.save(user);
    }
}
