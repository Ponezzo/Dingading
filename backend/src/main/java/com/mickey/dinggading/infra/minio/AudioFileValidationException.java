package com.mickey.dinggading.infra.minio;

public class AudioFileValidationException extends RuntimeException {

    public AudioFileValidationException(String message) {
        super(message);
    }

    public AudioFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}