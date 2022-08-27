package com.ms.wx.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WxServiceException extends ResponseStatusException {

    protected static final int DEFAULT_STATUS_CODE = 400;

    public WxServiceException(int statusCode) {
        super(HttpStatus.valueOf(statusCode));
    }
    public WxServiceException(HttpStatus status) {
        super(status);
    }

    public WxServiceException(int statusCode, String reason) {
        this(HttpStatus.valueOf(statusCode), reason);
    }

    public WxServiceException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public WxServiceException(String reason, Throwable cause) {
        this(DEFAULT_STATUS_CODE, reason, cause);
    }

    public WxServiceException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public WxServiceException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
