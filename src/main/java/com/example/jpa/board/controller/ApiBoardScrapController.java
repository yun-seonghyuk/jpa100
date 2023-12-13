package com.example.jpa.board.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.message.ResponseResult;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiBoardScrapController {

    private final BoardService boardService;


    /*
     * 75. 게시글의 스크랩을 추가하는 api
     * */
    @PutMapping("/board/{id}/scrap")
    public ResponseEntity<?> boardScrap(@PathVariable Long id,
                                        @RequestHeader("F-TOKEN") String token){

        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        }catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.result(boardService.scrapBoard(id, email));
    }

    /*
    * 76. 게시글의 스크랩을 삭제하는 api
    * */
    @DeleteMapping("/scrap/{id}")
    public ResponseEntity<?> deleteBoardScrap(@PathVariable Long id,
                                              @RequestHeader("F-TOKEN") String token){

        String email = "";
        try {
            email = JWTUtils.geIssuer(token);
        }catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }
        return ResponseResult.result(boardService.removeScrap(id, email));
    }
}
