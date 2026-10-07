package com.green.spring_board.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {
    int id;
    String title;
    String content;
    int hits;
    int likeCount;
    boolean isLikedByMe;
    Integer authorId;
    String authorNickname;
    LocalDateTime createdDatetime;
    LocalDateTime updatedDatetime;
}
