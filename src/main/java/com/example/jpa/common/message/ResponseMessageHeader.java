package com.example.jpa.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessageHeader {

    private boolean result;
    private String resultCode;
    private String message;
    private int status;

}
