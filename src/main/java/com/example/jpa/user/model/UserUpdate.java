package com.example.jpa.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserUpdate {

    @Size(max = 20, message = "연락처는 최대 20자 입니다.")
    @NotBlank(message = "연락처는 필수 항목 입니다.")
    private String phone;

}
