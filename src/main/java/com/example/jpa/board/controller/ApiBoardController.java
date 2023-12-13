package com.example.jpa.board.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.*;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.exception.BixException;
import com.example.jpa.common.message.ResponseResult;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.common.message.ResponseMessage;
import com.example.jpa.util.JWTUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiBoardController {

    private final BoardService boardService;

    /*
    61. 게시판 타입을 추가하는 api를 작성해 보세요.
    - 동일한 게시판 제목이 있는 경우 status: 200, result: false, message에 이미 동일한 게시판이 존재한다는 메시지 return
    - 게시판 이름은 필수항목에 대한 부분 체크
    * */
    @PostMapping("/board/type")
    public ResponseEntity<?> addBoardType(@RequestBody @Valid BoardTypeInput boardTypeInput,
                                          Errors errors){
        if(errors.hasErrors()){
            List<ResponseError> responseErrors = ResponseError.of(errors.getAllErrors());
            return new ResponseEntity<>(
                    ResponseMessage.fail("입력값이 정확하지 않습니다.",
                            responseErrors), HttpStatus.BAD_REQUEST);

        }
        ServiceResult result = boardService.addBoard(boardTypeInput);

        if(!result.isResult()) {
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().build();
    }

    /*
    62. 게시판 타입명을 수정하는 api
    * */
    @PutMapping("/board/type/{id}")
    public ResponseEntity<ResponseMessage> updateBoardType(
            @PathVariable Long id,
            @RequestBody @Valid BoardTypeInput boardTypeInput,
            Errors errors){

        if(errors.hasErrors()){
            List<ResponseError> responseErrors = ResponseError.of(errors.getAllErrors());
            return new ResponseEntity<>(
                    ResponseMessage.fail("입력값이 정확하지 않습니다.",
                            responseErrors), HttpStatus.BAD_REQUEST);

        }

        ServiceResult result = boardService.updateBoard(id, boardTypeInput);

        if(!result.isResult()) {
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().build();
    }

    /*
    * 63. 게사판타입 삭제하는 api
    * */
    @DeleteMapping("/board/type/{id}")
    public ResponseEntity<?> deleteBoardType(@PathVariable Long id){

        ServiceResult result = boardService.deleteBoard(id);

        if(!result.isResult()) {
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    /*
    * 64. 게시판타입의 목록을 return
    * */
    @GetMapping("/board/type")
    public ResponseEntity<?> boardType(){
        List<BoardType> boardTypeList = boardService.getAllBoardType();
        return ResponseEntity.ok().body(ResponseMessage.success(boardTypeList));
    }

    /*
    * 65. 게사판타입의 사용여부를 설정하는 api를 작성
    * */
    @PatchMapping("/board/type/{id}/using")
    public ResponseEntity<?> enableBoardType(@PathVariable Long id, @RequestBody BoardTypeUsing boardTypeUsing){
        ServiceResult result = boardService.setBoardTypeUsing(id, boardTypeUsing);

        if(!result.isResult()) {
            return ResponseEntity.ok().body(ResponseMessage.fail(result.getMessage()));
        }

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    /*
    * 66. 게시판별 작성된 게시글의 개수를 return api
    * - 현재 사용가능한 게시판의 대해서 게시글의 개수를 return
    * */
    @GetMapping("/board/type/count")
    public ResponseEntity<?> boardTypeCount(){
        return ResponseEntity.ok().body(boardService.getBoardTypeCount());
    }

    /*
    * 67. 게시된 게시글을 최상단에 배치하는 api
    * */
    @PatchMapping("/board/{id}/top")
    public ResponseEntity<?> boardPostTop(@PathVariable Long id){
        return ResponseEntity.ok().body(boardService.setBoardTop(id, true));
    }

    /*
    * 68. 최상단에 게시된 게시글을 최상단에서 해제하는 api
    * */
    @PatchMapping("/board/{id}/top/clear")
    public ResponseEntity<?> boardPostTopClear(@PathVariable Long id){
        return ResponseEntity.ok().body(boardService.setBoardTop(id, false));
    }

    /*
    * 69. 게시글의 게시시간을 시작일과 종료일로 설정하는 api
    * */
    @PatchMapping("/board/{id}/publish")
    public ResponseEntity<?> boardPeriod(@PathVariable Long id, @RequestBody BoardPeriod boardPeriod){
        ServiceResult result = boardService.setBoardPeriod(id, boardPeriod);

        if(!result.isResult()) {
            return ResponseResult.fail(result.getMessage());
        }
        return ResponseResult.success();
    }

    /*
    70. 게시글의 조회수를 증가시키는 api 를 작성해 보세요.
    다만, 동일 사용자 게시글 조회수 증가를 방지하는 부분에 대한 로직도 구현
     - jwt 인증을 통과한 사용자에 대해서 진행
    * */
    @PutMapping("/board/{id}/hits")
    public ResponseEntity<?> boardHits(@PathVariable Long id,
                                       @RequestHeader("F-TOKEN") String token){

        String email = "";

        try {
           email = JWTUtils.geIssuer(token);
        }catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        ServiceResult result = boardService.setBoardHits(id, email);
        if (result.isFail()) {
            return ResponseResult.fail(result.getMessage());
        }

        return ResponseResult.success();
    }

    /*
    * 71. 게시글에 대한 좋아요하기 기능을 수행하는 api
    * */
    @PutMapping("/board/{id}/like")
    public ResponseEntity<?> boardLike(@PathVariable Long id,
                                       @RequestHeader("F-TOKEN") String token){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        }catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.result(
                boardService.setBoardLike(id, email));
    }

    /*
    * 72. 게시글의 좋아요한 내용을 취소하는 api를 작성
    * */
    @PutMapping("/board/{id}/un-like")
    public ResponseEntity<?> boardUnLike(@PathVariable Long id,
                                       @RequestHeader("F-TOKEN") String token){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        }catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.result(
                boardService.setBoardUnLike(id, email));
    }

    /*
    * 73. 게시된 게시글에 대해서 문제가 있는 게시글을 신고하는 api
    * */

    @PutMapping("/board/{id}/badreport")
    public ResponseEntity<?> boardBadReport(@PathVariable Long id,
                                            @RequestHeader("F-TOKEN") String token,
                                            @RequestBody BoardBadReportInput boardBadReportInput){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        }catch (JWTVerificationException e) {
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.result(
                boardService.addBadReport(id, email, boardBadReportInput));
    }

    /*
    * 85. aop around를 이용하여 게시판 상세 조회에 대한 히스토리 기록하는 기능을 작성
    * */
    @GetMapping("/board/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){
        Board board = null;

        try {
            board = boardService.detail(id);
        }catch (BixException e) {
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.success(board);
    }

    /*
    * 92. 인터셉터를 이용하여 api 요청에 대한 정보를 log에 기록하는 기능
    * */
    @GetMapping("/board")
    public ResponseEntity<?> list(){
        return ResponseResult.success(boardService.list());
    }


    /*
     * 93. 인터셉터를 활용하여 JWT 인증이 필요한 api에 대해서(글쓰기) 토큰 유효성을 검증하는 api를 작성해 보세요.
     * - 게시글쓰기 기능구현
     * 글쓰기 api호출시 토큰 유효성 검사
     * */
    @PostMapping("/board")
    public ResponseEntity<?> add(@RequestBody BoardInput boardInput,
                                 @RequestHeader("F-TOKEN") String token){

        String email = JWTUtils.geIssuer(token);
        return ResponseResult.result(boardService.add(email, boardInput));
    }

    /*
    * 97. 게시판에 글을 작성했을때 사용자에게 작성된 글의 정보를 메일로 전송하는 api
    * */
    @PostMapping("/board2")
    public ResponseEntity<?> add2(@RequestBody BoardInput boardInput,
                                 @RequestHeader("F-TOKEN") String token){

        String email = JWTUtils.geIssuer(token);
        return ResponseResult.result(boardService.add(email, boardInput));
    }

    /*
    * 98. 문의 게시판이 글에 답변을 달았을때 메일로 답변 정보를 전송하는 api
    * */
    @PostMapping("/admin/board/{id}/reply")
    public ResponseEntity<?> reply(@PathVariable Long id,
                                   @RequestBody BoardReplyInput boardReplyInput,
                                   @RequestHeader("F-TOKEN") String token){

        String email = JWTUtils.geIssuer(token);
        return ResponseResult.result(boardService.replyBoard(id, boardReplyInput));
    }










}
