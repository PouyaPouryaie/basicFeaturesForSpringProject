package ir.bigz.springbootreal.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.bigz.springbootreal.exception.validation.ValidationErrorResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class HttpExceptionModel {

    private String uuid;
    private final int errorCode;
    private final String message;
    private final String timestamp;
    @JsonIgnore
    private final ValidationErrorResponseModel validationError;
}
