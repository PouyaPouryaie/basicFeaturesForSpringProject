package ir.bigz.springbootreal.exception;

import java.time.ZonedDateTime;

public class HttpExceptionModel {

    private final String message;
    private final HttpErrorCode httpErrorCode;
    private final String timestamp;

    public HttpExceptionModel(String message, HttpErrorCode httpErrorCode, String timestamp) {
        this.message = message;
        this.httpErrorCode = httpErrorCode;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public HttpErrorCode getHttpErrorCode() {
        return httpErrorCode;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
