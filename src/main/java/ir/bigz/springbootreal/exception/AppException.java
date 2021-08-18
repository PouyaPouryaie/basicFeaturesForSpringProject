package ir.bigz.springbootreal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AppException extends ResponseStatusException{

    private HttpErrorCode httpErrorCode;
    private String detail;


    static public AppException newInstance(HttpStatus status) {
        return new AppException(status, null, null, null, null);
    }

    static public AppException newInstance(HttpStatus status, String message) {
        return new AppException(status, message, null, null, null);
    }

    static public AppException newInstance(HttpStatus status, String message, Throwable cause) {
        return new AppException(status, message, cause, null, null);
    }

    static public AppException newInstance(HttpStatus status, String message, Throwable cause, HttpErrorCode errorCode) {
        return new AppException(status, message, cause, errorCode, null);
    }

    static public AppException newInstance(HttpErrorCode errorCode, String message, Throwable cause) {
        return new AppException(errorCode.getStatus(), message, cause, errorCode, null);
    }

    static public AppException newInstance(HttpErrorCode errorCode, String detail) {
        return new AppException(errorCode.getStatus(), null, null, errorCode, detail);
    }


    public AppException(HttpStatus httpStatus, String message, Throwable cause, HttpErrorCode errorCode, String detail){
        super(httpStatus, message, cause);
        this.httpErrorCode = errorCode;
        this.detail = detail;
    }

    public HttpErrorCode getHttpErrorCode() {
        return httpErrorCode;
    }

    public String getDetail() {
        return detail;
    }
}