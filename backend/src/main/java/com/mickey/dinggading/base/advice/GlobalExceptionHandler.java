package com.mickey.dinggading.base.advice;

import com.mickey.dinggading.base.code.ErrorReasonDTO;
import com.mickey.dinggading.exception.BaseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException ex, WebRequest request) {
        ErrorReasonDTO errorReason = ex.getErrorReasonHttpStatus();
        HttpStatus status = errorReason.getHttpStatus();

        ProblemDetail problemDetail = createProblemDetail(ex, status, errorReason, request);

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("서버 내부 오류가 발생했습니다");

        Map<String, Object> properties = new HashMap<>();
        properties.put("timestamp", LocalDateTime.now().toString());
        properties.put("code", "SERVER5000");
        properties.put("isSuccess", false);

        problemDetail.setProperties(properties);

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        ex.printStackTrace();

        if (body == null) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(statusCode, ex.getMessage());
            problemDetail.setTitle("요청 처리 중 오류가 발생했습니다");

            Map<String, Object> properties = new HashMap<>();
            properties.put("timestamp", LocalDateTime.now().toString());
            properties.put("code", "REQUEST4000");
            properties.put("isSuccess", false);

            problemDetail.setProperties(properties);
            body = problemDetail;
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ProblemDetail createProblemDetail(Exception ex, HttpStatus status, ErrorReasonDTO errorReason,
                                              WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(errorReason.getMessage());

        Map<String, Object> properties = new HashMap<>();
        properties.put("timestamp", LocalDateTime.now().toString());
        properties.put("code", errorReason.getCode());
        properties.put("isSuccess", errorReason.getIsSuccess());

        problemDetail.setProperties(properties);

        return problemDetail;
    }
}