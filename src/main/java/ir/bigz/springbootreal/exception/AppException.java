package ir.bigz.springbootreal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AppException extends ResponseStatusException{

    private final SampleExceptionType sampleExceptionType;
    private final String detail;


    static public AppException newInstance(HttpStatus status) {
        return new AppException(status, null, null, null, null);
    }

    static public AppException newInstance(HttpStatus status, String message) {
        return new AppException(status, message, null, null, null);
    }

    static public AppException newInstance(HttpStatus status, String message, Throwable cause) {
        return new AppException(status, message, cause, null, null);
    }

    static public AppException newInstance(HttpStatus status, String message, Throwable cause, SampleExceptionType exceptionType) {
        return new AppException(status, message, cause, exceptionType, null);
    }

    static public AppException newInstance(SampleExceptionType exceptionType, String message, Throwable cause) {
        return new AppException(exceptionType.getHttpStatus(), message, cause, exceptionType, null);
    }

    static public AppException newInstance(SampleExceptionType exceptionType, String detail) {
        return new AppException(exceptionType.getHttpStatus(), null, null, exceptionType, detail);
    }


    public AppException(HttpStatus httpStatus, String message, Throwable cause, SampleExceptionType exceptionType, String detail){
        super(httpStatus, message, cause);
        this.sampleExceptionType = exceptionType;
        this.detail = detail;
    }

    public SampleExceptionType getSampleExceptionType() {
        return sampleExceptionType;
    }

    public String getDetail() {
        return detail;
    }
}