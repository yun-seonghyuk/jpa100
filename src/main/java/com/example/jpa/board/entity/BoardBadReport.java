package com.example.jpa.board.entity;

import com.example.jpa.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BoardBadReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고자 정보
    @Column private Long userId;
    @Column private String userName;
    @Column private String userEmail;

    // 신고 게시글 정보
    @Column private Long boardId;
    @Column private Long boardUserId;
    @Column private String boardTitle;
    @Column private String boardContents;
    @Column private LocalDateTime boardRegDate;

    // 신고 내용
    @Column private String comment;
    @Column private LocalDateTime regDate;

}
