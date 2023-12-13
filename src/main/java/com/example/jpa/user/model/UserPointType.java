package com.example.jpa.user.model;

import lombok.Getter;

@Getter
public enum UserPointType {
    NONE(0),
    USER_REGISTER(100),
    ADD_POST(200),
    ADD_COMMENT(150),
    ADD_LIKE(50);

    private final int value;

    UserPointType(int value) {
       this.value = value;
    }
}
