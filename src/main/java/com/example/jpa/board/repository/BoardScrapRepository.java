package com.example.jpa.board.repository;

import com.example.jpa.board.entity.BoardScrap;
import com.example.jpa.board.entity.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardScrapRepository extends JpaRepository<BoardScrap, Long> {
}
