package com.example.jpa.board.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardTypeInput {

    @NotBlank(message = "게시판 항목은 필수입니다.")
    private String name;
}
