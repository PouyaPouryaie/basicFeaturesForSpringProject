package ir.bigz.springbootreal.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionType {
    HttpStatus getHttpStatus();
    int getErrorCode();
    String getReasonMessage();
}
