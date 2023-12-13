package com.example.jpa.user.cotroller;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.common.message.ResponseResult;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiUserInterestController {

    private final UserService userService;

    /*
    * 78. 관심사용자에 등록하는 api를 작성
    * */
    @PutMapping("/user/{id}/interest")
    public ResponseEntity<?> interestUser(@PathVariable Long id,
                                               @RequestHeader("F-TOKEN") String token){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        } catch (SignatureVerificationException e){
            return new ResponseEntity<>("토큰 정보가 정확하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        ServiceResult result = userService.addInterestUser(id,email);
        return ResponseResult.result(result);
    }

    /*
    * 79. 관심사용자에 등록하는 api
    * */
    @DeleteMapping("/user/{id}/interest")
    public ResponseEntity<?> deleteInterestUser(@PathVariable Long id,
                                          @RequestHeader("F-TOKEN") String token){
        String email = "";

        try {
            email = JWTUtils.geIssuer(token);
        } catch (SignatureVerificationException e){
            return new ResponseEntity<>("토큰 정보가 정확하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        ServiceResult result = userService.removeInterest(id,email);
        return ResponseResult.result(result);
    }
}
