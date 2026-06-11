package com.mickey.dinggading.exception;

import com.mickey.dinggading.base.code.BaseErrorCode;

public class ExceptionHandler extends BaseException {

    public ExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}