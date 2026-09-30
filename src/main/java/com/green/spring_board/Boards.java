package com.green.spring_board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// JPA 기능
@Entity
@Table(name = "boards")
// lombok 기능
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Boards {
    // DB에서 어떤 콜럼이 어떤 그거인지 알려주는거
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int hits;
}
