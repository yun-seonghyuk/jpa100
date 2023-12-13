package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.*;

import java.util.List;

public interface UserService {

    UserSummary getUserStatusCount();

    List<User> getTodayUsers();

    List<UserNoticeCount> getUserNoticeCount();

    List<UserLogCount> getUserLogCount();

    // 좋아요를 가장 많이 한 사용자 목록
    List<UserLogCount> getBestLikeUser();

    ServiceResult addInterestUser(Long id, String email);

    ServiceResult removeInterest(Long interestId, String email);

    /*
    로그인 정보 확인
    * */
    User login(UserLogin userLogin);

    // 회원 가입에
    ServiceResult addUser(UserInput userInput);

    ServiceResult resetPassword(UserPasswordResetInput userPasswordResetInput);

    void sendServiceNotice();
}
