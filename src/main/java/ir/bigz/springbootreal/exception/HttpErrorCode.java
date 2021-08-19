package ir.bigz.springbootreal.exception;

import org.springframework.http.HttpStatus;

public enum HttpErrorCode {

    ERR_10700	(10700, "Invalid Entity For Persist", HttpStatus.BAD_REQUEST),
    ERR_10701	(10701, "process of the request has been error", HttpStatus.BAD_REQUEST),
    ERR_10702	(10702, "User Not Found", HttpStatus.NOT_FOUND),
    ERR_10703	(10703, "Invalid Entity For Update", HttpStatus.BAD_REQUEST);

    private final int code;
    private final HttpStatus status;
    private final String reason;

    HttpErrorCode(int code, String reason, HttpStatus status) {
        this.code = code;
        this.status = status;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
