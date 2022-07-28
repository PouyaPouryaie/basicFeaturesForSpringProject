package ir.bigz.springbootreal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@ControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<Object> handleApiRequestException(AppException e){
        UUID uuid = UUID.randomUUID();

        HttpExceptionModel apiException = new HttpExceptionModel(uuid.toString(),
                e.getHttpErrorCode().getErrorCode(),  e.getDetail(),
                timeLog(), null);

        return new ResponseEntity<>(apiException, e.getHttpErrorCode().getHttpStatus());
    }

    private String timeLog(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
        return ZonedDateTime.now(ZoneId.of("Asia/Tehran")).format(formatter);
    }

}
