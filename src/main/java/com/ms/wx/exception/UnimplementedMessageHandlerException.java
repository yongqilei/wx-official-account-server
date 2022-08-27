package com.ms.wx.exception;

import org.springframework.http.HttpStatus;

public class UnimplementedMessageHandlerException extends WxServiceException{

    private static final String DEFAULT_MESSAGE = "This type of message handler has not been implemented yet.";

    public UnimplementedMessageHandlerException(HttpStatus status) {
        super(status, DEFAULT_MESSAGE);
    }

    public UnimplementedMessageHandlerException(HttpStatus status, Throwable cause) {
        this(status, DEFAULT_MESSAGE, cause);
    }

    public UnimplementedMessageHandlerException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public UnimplementedMessageHandlerException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
