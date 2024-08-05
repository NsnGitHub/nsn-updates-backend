package com.nsn.nsnupdatesbackend.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class APIException {
    private final String path;
    private final HttpStatus status;
    private final String message;
    private final ZonedDateTime timestamp;

    public APIException(String path, HttpStatus status, String message, ZonedDateTime timestamp) {
        this.path = path;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
