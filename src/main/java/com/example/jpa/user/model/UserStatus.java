package com.example.jpa.user.model;

public enum UserStatus {
    None, Using, stop;

    int value;

    UserStatus(){}

    public int getValue(){
        return this.value;
    }
}
