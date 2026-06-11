package com.mickey.dinggading.base.code;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@ToString
public class ErrorReasonDTO {

    private final boolean isSuccess;
    private final String code;
    private final String message;
    private HttpStatus httpStatus;

    public boolean getIsSuccess() {
        return isSuccess;
    }
}