package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.model.UserPointInput;

public interface PointService {

    ServiceResult addPoint(String email, UserPointInput userPointInput);
}
