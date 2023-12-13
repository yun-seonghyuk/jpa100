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
public class BoardScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    // 스크랩 글 정보
    @Column private Long boardId;
    @Column private Long boardTypeId;
    @Column private Long boardUserId;
    @Column private String boardTitle;
    @Column private String boardContents;
    @Column private LocalDateTime boardRegDate;

    @Column private LocalDateTime regDate;

}
