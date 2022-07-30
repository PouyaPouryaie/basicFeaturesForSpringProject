package ir.bigz.springbootreal.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class CustomExceptionType implements ExceptionType{
    HttpStatus httpStatus;
    int errorCode;
    String reasonMessage;
}
