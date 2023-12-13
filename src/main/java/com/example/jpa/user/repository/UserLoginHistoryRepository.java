package com.example.jpa.user.repository;

import com.example.jpa.user.entity.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {

}
