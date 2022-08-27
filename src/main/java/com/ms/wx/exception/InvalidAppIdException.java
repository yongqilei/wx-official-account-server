package com.ms.wx.exception;

import org.springframework.http.HttpStatus;

public class InvalidAppIdException extends WxServiceException{

    private static final String DEFAULT_MESSAGE = "Invalid App Id";

    public InvalidAppIdException() {
        this(DEFAULT_STATUS_CODE, DEFAULT_MESSAGE);
    }

    public InvalidAppIdException(int rawStatusCode, String reason) {
        this(HttpStatus.valueOf(rawStatusCode), reason);
    }
    public InvalidAppIdException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public InvalidAppIdException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public InvalidAppIdException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
