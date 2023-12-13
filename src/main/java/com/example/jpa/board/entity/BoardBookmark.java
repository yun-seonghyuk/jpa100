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
public class BoardBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column private Long boardId;
    @Column private Long boardTypeId;
    @Column private String boardTitle;
    @Column private String boardUrl;

    @Column private LocalDateTime regDate;

}
