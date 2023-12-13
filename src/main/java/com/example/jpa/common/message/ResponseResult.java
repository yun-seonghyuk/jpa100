package com.example.jpa.common.message;

import com.example.jpa.board.model.ServiceResult;
import org.springframework.http.ResponseEntity;

public class ResponseResult {

    public static ResponseEntity<?> fail(String message) {
        return fail(message, null);
    }

    public static ResponseEntity<?> fail(String message, Object data) {
        return ResponseEntity.badRequest().body(ResponseMessage.fail(message, data));
    }

    public static ResponseEntity<?> success() {
        return success(null);
    }

    public static ResponseEntity<?> success(Object data) {
        return ResponseEntity.ok().body(ResponseMessage.success(data));
    }

    public static ResponseEntity<?> result(ServiceResult result) {
        if (result.isFail()) {
            return ResponseResult.fail(result.getMessage());
        }
        return success();
    }

}
