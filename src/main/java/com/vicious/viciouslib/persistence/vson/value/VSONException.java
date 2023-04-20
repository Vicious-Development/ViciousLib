package com.vicious.viciouslib.persistence.vson.value;

public class VSONException extends RuntimeException {
    public VSONException() {
    }

    public VSONException(String message) {
        super(message);
    }

    public VSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public VSONException(Throwable cause) {
        super(cause);
    }

    public VSONException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
