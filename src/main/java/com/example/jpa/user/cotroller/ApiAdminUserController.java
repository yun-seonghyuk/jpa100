package com.example.jpa.user.cotroller;

import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserLoginHistory;
import com.example.jpa.common.message.ResponseMessage;
import com.example.jpa.user.model.UserSearch;
import com.example.jpa.user.model.UserStatusInput;
import com.example.jpa.user.repository.UserLoginHistoryRepository;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class ApiAdminUserController {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;

    private final UserService userService;

    // 48. 사용자 목록 과 사용자 수를 함께 내리는 rest api
//    @GetMapping("/api/admin/user")
//    public ResponseMessage userList(){
//
//        List<User> userList = userRepository.findAll();
//        long totalCount = userRepository.count();
//
//        return ResponseMessage.builder()
//                .totalCount(totalCount)
//                .data(userList)
//                .build();
//    }

    // 49. 사용자 상세 조회하는 api
    @GetMapping("/api/admin/user/{id}")
    public ResponseEntity<?> userDetail(@PathVariable Long id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body(ResponseMessage.success(user));
    }

    // 50. 사용자 목록 조회에 대한 검색을 return

    @GetMapping("/api/admin/user/search")
    public ResponseEntity<?> findUser(@RequestBody UserSearch userSearch) {
        // email like '%' || email || '%'

        List<User> userList = userRepository.findByEmailContainsOrPhoneContainsOrUserNameContains(
                userSearch.getEmail(),
                userSearch.getPhone(),
                userSearch.getUserName());

        return ResponseEntity.ok().body(ResponseMessage.success(userList));
    }
    // 51. 사용자의 상태를 변경하는 api

    @PatchMapping("/api/admin/user/{id}/status")
    public ResponseEntity<?> userStatus(@PathVariable Long id, @RequestBody UserStatusInput userStatusInput) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."),
                    HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();
        user.setStatus(userStatusInput.getStatus());

        return ResponseEntity.ok().body(userRepository.save(user));
    }

    /*
     *  52. 사용자 정보를 삭제하는 api 작성
     *  - 작성된 게시글이 있으면 예외 발생
     * */

    @DeleteMapping("/api/admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다.")
                    , HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (noticeRepository.countByUser(user) > 0) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자가 작성한 공지사항이 있습니다.")
                    , HttpStatus.BAD_REQUEST);
        }

        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }

    /*
     * 53. 사용자가 로그인을 했을때 이에 대한 접속 이력이 저장된다고 했을때, 이에 대한 접속이력을 조회하는 api
     * */

    @GetMapping("/api/admin/user/login/history")
    public ResponseEntity<?> userLoginHistory() {
        List<UserLoginHistory> userLoginHistories = userLoginHistoryRepository.findAll();

        return ResponseEntity.ok().body(userLoginHistories);
    }

    /*
        54. 사용자의 접속을 제한하는 api를 구현
    * */

    @PatchMapping("/api/admin/user/{id}/lock")
    public ResponseEntity<?> userLock(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다.")
                    , HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (user.isLockYn()) {
            return new ResponseEntity<>(ResponseMessage.fail("이미 접속제한이 된 사용자 입니다.")
                    , HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(true);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    /*
    55. 사용자의 접속제한을 해제하는 api를 구현
    * */

    @PatchMapping("/api/admin/user/{id}/un-lock")
    public ResponseEntity<?> userUnLock(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다.")
                    , HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (!user.isLockYn()) {
            return new ResponseEntity<>(ResponseMessage.fail("이미 접속제한이 해제된 사용자 입니다.")
                    , HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(false);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    /*
    56. 회원 전체수와 상태별 회원수에 대한 정보를 return
    * */
    @GetMapping("/api/admin/user/status/count")
    public ResponseEntity<?> userStatusCount() {
        return ResponseEntity.ok()
                .body(ResponseMessage.success(userService.getUserStatusCount()));
    }

    /*
    57. 오늘의 사용자 가입 목록을 리턴하는 api
    * */
    @GetMapping("/api/admin/user/today")
    public ResponseEntity<?> todayUser(){
        return ResponseEntity.ok()
                .body(ResponseMessage.success(userService.getTodayUsers()));
    }

    /*
    58. 사용자별 공지사항의 게시글 수를 return
    * */
    @GetMapping("/api/admin/user/notice/count")
    public ResponseEntity<?> userNoticeCount(){
        return ResponseEntity.ok().body(
                ResponseMessage.success(userService.getUserNoticeCount()));
    }

    /*
    59. 사용자별 게시글수와 좋아요수를 리턴하는 api
    * */
    @GetMapping("/api/admin/user/log/count")
    public ResponseEntity<?> userLogCount(){
        return ResponseEntity.ok().body(
                ResponseMessage.success(userService.getUserLogCount()));
    }

    /*
    60. 좋아요를 가장 많이한 사용자 목록
    * */
    @GetMapping("/api/admin/user/like/best")
    public ResponseEntity<?> bestLikeCount(){
        return ResponseEntity.ok().body(
                ResponseMessage.success(userService.getBestLikeUser()));
    }
}
