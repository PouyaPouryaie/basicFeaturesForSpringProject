package ir.bigz.springbootreal.exception.validation;

import ir.bigz.springbootreal.exception.HttpErrorCode;
import ir.bigz.springbootreal.exception.HttpExceptionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpErrorCode.ERR_10704.getStatus())
                .body(HttpExceptionModel.builder()
                        .httpErrorCode(HttpErrorCode.ERR_10704)
                        .message(HttpErrorCode.ERR_10704.getReason())
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
