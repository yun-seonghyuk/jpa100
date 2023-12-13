package com.example.jpa.board.controller;

import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.message.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiAdminBoardController {

    private final BoardService boardService;


    /*
    * 74. 게시글의 신고하기 목록을 조회하는 api
    * */
    @GetMapping("/admin/board/bad-report")
    public ResponseEntity<?> badReport(){
        List<BoardBadReport> list = boardService.badReportList();
        return ResponseResult.success(list);
    }



}
