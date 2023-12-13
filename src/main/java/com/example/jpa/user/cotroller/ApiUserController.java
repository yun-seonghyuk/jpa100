package com.example.jpa.user.cotroller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.exception.BixException;
import com.example.jpa.common.message.ResponseResult;
import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.entity.NoticeLike;
import com.example.jpa.notice.model.NoticeResponse;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeLikeRepository;
import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.exception.ExistsEmailException;
import com.example.jpa.user.exception.PasswordNotMatchException;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.*;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.PointService;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import com.example.jpa.util.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApiUserController {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeLikeRepository noticeLikeRepository;
    private final BoardService boardService;
    private final PointService pointService;
    private final UserService userService;


    // 31 ~ 32 사용자 정보 입력
    @PostMapping("/api/user")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors){
        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError)e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .password(userInput.getPassword())
                .phone(userInput.getPhone())
                .regDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // 33 사용자 정보 수정
    @PutMapping("/api/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                    @RequestBody @Valid UserUpdate userUpdate,
                                    Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError)e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        user.setPhone(userUpdate.getPhone());
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // 에러 핸들링
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> UserNotFoundExceptionHandler(UserNotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 34. 사용자 정보 조회
    @GetMapping("/api/user/{id}")
    public UserResponse getUser(@PathVariable Long id){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        return UserResponse.of(user);
    }

    // 35. 사용자 내가 쓴 정보
    @GetMapping("/api/user/{id}/notice")
    public List<NoticeResponse> userNotice(@PathVariable Long id){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        List<Notice> noticeList = noticeRepository.findByUser(user);

        List<NoticeResponse> noticeResponseList = new ArrayList<>();

        noticeList.forEach((e) ->{
            noticeResponseList.add(NoticeResponse.of(e));
        });

        return noticeResponseList;
    }

    // 36 사용자 등록시 이미 존재하는 이메일
    @PostMapping("/api/user2")
    public ResponseEntity<?> addUser2(@RequestBody @Valid UserInput userInput, Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        if(userRepository.countByEmail(userInput.getEmail()) > 0) {
            throw new ExistsEmailException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .phone(userInput.getPhone())
                .password(userInput.getPassword())
                .regDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {ExistsEmailException.class, PasswordNotMatchException.class} )
    public ResponseEntity<?> ExistsEmailExceptionHandler(RuntimeException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 37. 사용자 비밀번호 수정
    @PatchMapping("/api/user/{id}/password")
    public ResponseEntity<?> updateUserPassword(
                                   @PathVariable Long id,
                                   @RequestBody UserInputPassword userInputPassword,
                                   Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByIdAndPassword(id, userInputPassword.getPassword())
                .orElseThrow(()-> new PasswordNotMatchException("비밀번호가 일치하지 않습니다."));

        user.setPassword(userInputPassword.getNewPassword());
        user.setUpdateDate(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // 38. 비밀번호 암호화

    private String getEncryptPassword(String password){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    @PostMapping("/api/user3")
    public ResponseEntity<?> addUser3(@RequestBody @Valid UserInput userInput, Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        if(userRepository.countByEmail(userInput.getEmail()) > 0) {
            throw new ExistsEmailException("이미 존재하는 이메일입니다.");
        }

        String encryptPassword = getEncryptPassword(userInput.getPassword());

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .phone(userInput.getPhone())
                .password(encryptPassword)
                .regDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // 39. 사용자 회원탈퇴 기능 회원정보가 존재하지 않는경우 예외처리

    @DeleteMapping("/api/user/{id}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        // 내가 쓴 공지사항이 있는 경우
        // -> ???
        // 1. 삭제 못해.. 삭제하려면, 공지사항 삭제화고와..
        // 2. 회원 삭제 전에 공지사항글을 다 삭제하는 경우

        String message = null;
        try {
            userRepository.delete(user);
        } catch (DataIntegrityViolationException e) {
            message = "제약조건에 문제가 있습니다.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            message = "회원 탈퇴 중 문제가 발생하였습니다.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    // 40. 사용자 아이디(이메일)를 찾는 API 를 작성해 보세요.
    // 이름과 전화번에 해당하는 이메일을 찾는다.

    @GetMapping("/api/user")
    public ResponseEntity<?> findUser(@RequestBody UserInputFind userInputFind){

        User user = userRepository.findByUserNameAndPhone(userInputFind.getUserName(), userInputFind.getPhone())
                .orElseThrow(()-> new UserNotFoundException("사용자 정보가 없습니다."));

        UserResponse userResponse = UserResponse.of(user);

        return ResponseEntity.ok().body(userResponse.getEmail());
    }

    // 41. 사용자 비밀번호 초기화

    private String getResetPassword(){
        return UUID.randomUUID().toString().replaceAll("-","").substring(0, 10);
    }

    @GetMapping("/api/user/{id}/password/reset")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id){

        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("사용자 정보가 없습니다."));

        // 비밀번호 초기화
        String resetPassword = getResetPassword();
        String resetEncryptPassword = getEncryptPassword(getResetPassword());

        user.setPassword(resetEncryptPassword);
        userRepository.save(user);

        String message = String.format("[%s] 님의 임시 비밀번호가  [%s] 로 초기화 되었습니다",
                user.getUserName(),
                resetPassword);

        sendSMS(message);

        return ResponseEntity.ok().build();
    }

    void sendSMS(String message){
        System.out.println("[문자 메세지 전송]");
        System.out.println(message);
    }

    // 42. 내가 좋아요한 공지사항을 보는 api

    @GetMapping("/api/user/{id}/notice/like")
    public List<NoticeLike> likeNotice(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("사용자 정보가 없습니다."));

        return noticeLikeRepository.findByUser(user);
    }


 /*
 * 43 ~ 44. 사용자 이메일과 비밀번호를 통해서 jwt을 발행하는 api
 * 45. 제한기간 1개월
 * */

    @PostMapping("/api/user/login")
    public ResponseEntity<?> createToken(@RequestBody UserLogin userLogin, Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(()-> new UserNotFoundException("사용자 정보가 없습니다."));

        if (!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())){
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발행 시점

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String token = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id", user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("fastcampus".getBytes()));

        return ResponseEntity.ok().body( UserLoginToken.builder()
                .token(token)
                .build());
    }

    // 46. jwt 토큰 재발행 , 사용자정보가 있는지 확인

    @PatchMapping("/api/user/login")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){

        String token = request.getHeader("F-TOKEN");
        String email = "";

        try {
             email = JWT.require(Algorithm.HMAC512("fastcampus".getBytes()))
                    .build()
                    .verify(token)
                    .getIssuer();
        } catch (SignatureVerificationException e){
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("사용자 정보가 없습니다."));

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String newToken = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id", user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("fastcampus".getBytes()));

        return ResponseEntity.ok().body( UserLoginToken.builder()
                .token(newToken)
                .build());
    }

    // 47. 토큰 삭제
    @DeleteMapping("/api/user/login")
    public ResponseEntity<?> removeToken(@RequestHeader("F-TOKEN") String token){

        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        } catch (SignatureVerificationException e){
            return new ResponseEntity<>("토큰 정보가 정확하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 세션, 쿠키삭제
        // 클라이언트 쿠키/로컬스토리지/세션스토리지
        // 블랙리스트 작성
        return ResponseEntity.ok().build();
    }


    /*
    * 80. 내가 작성한 게시글 목록을 return 하는 api
    * */
    @GetMapping("/api/user/board/post")
    public ResponseEntity<?> myPost(@RequestHeader("F-TOKEN") String token){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        } catch (SignatureVerificationException e){
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.success(boardService.postList(email));
    }

    /*
    * 81. 내가 작성한 게시글의 코멘트 목록을 return 하는 api
    * */
    @GetMapping("/api/user/board/comment")
    public ResponseEntity<?> myComment(@RequestHeader("F-TOKEN") String token){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        } catch (SignatureVerificationException e){
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.success(boardService.commentList(email));

    }

    /*
    *  82. 사용자의 포인트 정보를 만들고 게시글을 작성할 경우, 포인트를 누적하는 api
    * */
    @PostMapping("/api/user/point")
    public ResponseEntity<?> userPoint(@RequestHeader("F-TOKEN") String token,
                                       @RequestBody UserPointInput userPointInput){

        String email = "";
        try {
            email = JWTUtils.geIssuer(token);
        } catch (SignatureVerificationException e){
            return ResponseResult.fail("토큰 정보가 정확하지 않습니다.");
        }

        return ResponseResult.result(pointService.addPoint(email, userPointInput));
    }

    /*
    * 95. 회원가입시 가입된 회원에게 가입메일을 전송하는 api
    * */
    @PostMapping("/api/public/user")
    public ResponseEntity<?> addUser(@RequestBody UserInput userInput){
        return ResponseResult.result(userService.addUser(userInput));
    }

    /*
    * 96. 비밀번호를 초기화를 위해서 이메일로 인증코드를 전송하는 api
    * */
    @PostMapping("/api/public/user/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody UserPasswordResetInput userPasswordResetInput,
                                           Errors errors){
        if (errors.hasErrors()) {
            return ResponseResult.fail("입력값이 정확하지 않습니다.",
                    ResponseError.of(errors.getAllErrors()));
        }

        ServiceResult result = null;
        try {
            result = userService.resetPassword(userPasswordResetInput);
        } catch (BixException e) {
            return ResponseResult.fail(e.getMessage());
        }

        return ResponseResult.result(result);
    }

}
