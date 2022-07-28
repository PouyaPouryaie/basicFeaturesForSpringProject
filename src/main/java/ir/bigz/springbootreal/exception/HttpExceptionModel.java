package ir.bigz.springbootreal.exception;

import ir.bigz.springbootreal.exception.validation.ValidationErrorResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HttpExceptionModel {

    private String uuid;
    private final int errorCode;
    private final String message;
    private final String timestamp;
    private final ValidationErrorResponseModel validationError;
}
