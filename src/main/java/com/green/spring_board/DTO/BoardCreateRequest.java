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

public class BoardCreateRequest {
    @NotBlank
    @Size(min = 10, max = 50)
    private String title;
    @NotBlank
    @Size(min = 10)
    private String content;
}
