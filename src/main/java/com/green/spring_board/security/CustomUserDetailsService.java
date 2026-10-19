package com.green.spring_board.security;

import com.green.spring_board.entity.User;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 유저네임을 전달받아 DB내에 존재하는지 확인
        User user = userRepository.findByUsername(username);

        // 해당 유저를 찾을 수 없는 경우
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        // 사용중인 회원이 아닌 경우
        if (user.getState() != UserState.ACTIVE) {
            throw new UsernameNotFoundException("사용할 수 없는 계정입니다");
        }

        // 엔티티 User가 아닌 프레임워크의 Security 유저 반환하도록 주의!
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
//                .authorities(user.getRole().toString())
                .build();
    }
}
