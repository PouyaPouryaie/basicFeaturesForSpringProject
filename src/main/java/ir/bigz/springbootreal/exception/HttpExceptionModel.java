package ir.bigz.springbootreal.exception;

import ir.bigz.springbootreal.exception.validation.ValidationErrorResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HttpExceptionModel {

    private final String message;
    private final HttpErrorCode httpErrorCode;
    private final String timestamp;
    private final ValidationErrorResponseModel validationError;
}
