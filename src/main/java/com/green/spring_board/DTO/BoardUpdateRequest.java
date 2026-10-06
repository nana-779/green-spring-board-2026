package com.green.spring_board.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BoardUpdateRequest {
    @Size(min = 10, max = 50)
    private String title;
    @Size(min = 10)
    private String content;
}
