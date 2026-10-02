package com.green.spring_board.repository;


import com.green.spring_board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Integer>{
    // JPA 쿼리메소드 기능
    // 이름만으로 기능을 예측해서 쿼리 생성
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
