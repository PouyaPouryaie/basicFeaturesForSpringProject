package ir.bigz.springbootreal.exception;

import ir.bigz.springbootreal.validation.annotation.ValidationLogResponseHandled;
import ir.bigz.springbootreal.exception.validation.ValidationErrorResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<HttpExceptionModel> handleApiRequestException(AppException e) {

        return ResponseEntity.status(e.getSampleExceptionType().getHttpStatus())
                .body(HttpExceptionModel.builder()
                        .uuid(UUID.randomUUID().toString())
                        .errorCode(e.getSampleExceptionType().getErrorCode())
                        .message(e.getDetail())
                        .timestamp(timeLog()).build());
    }


    @ValidationLogResponseHandled
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(SampleExceptionType.VALIDATION_ERROR.getHttpStatus())
                .body(HttpExceptionModel.builder()
                        .uuid(UUID.randomUUID().toString())
                        .errorCode(SampleExceptionType.VALIDATION_ERROR.getErrorCode())
                        .message(SampleExceptionType.VALIDATION_ERROR.getReasonMessage())
                        .timestamp(timeLog())
                        .validationError(ValidationErrorResponseModel.builder()
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build()).build());
    }

    private String timeLog() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
        return ZonedDateTime.now(ZoneId.of("Asia/Tehran")).format(formatter);
    }

}
