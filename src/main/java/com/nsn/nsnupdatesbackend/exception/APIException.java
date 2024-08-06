package com.nsn.nsnupdatesbackend.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@JsonSerialize(using = APIExceptionSerializer.class)
public class APIException extends RuntimeException {
    private final String path;
    private final HttpStatus status;
    private final ZonedDateTime timestamp;

    public APIException(String path, HttpStatus status, String message, ZonedDateTime timestamp) {
        super(message);
        this.path = path;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
