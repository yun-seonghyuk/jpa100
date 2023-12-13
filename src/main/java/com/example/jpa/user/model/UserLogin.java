package com.example.jpa.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserLogin {

    @NotBlank(message = "이메일 항목은 필수 입니다.")
    private String email;

    @NotBlank(message = "비밀번호 항목은 필수 입니다.")
    private String password;

}
