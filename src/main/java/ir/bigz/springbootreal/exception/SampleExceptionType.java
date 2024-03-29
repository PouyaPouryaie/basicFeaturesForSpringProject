package ir.bigz.springbootreal.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

public enum SampleExceptionType implements ExceptionType {

    INVALID_ENTITY_FOR_INSERT  (10700, "invalid_entity_for_insert", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR             (10701, "internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND             (10702, "user_not_found", HttpStatus.NOT_FOUND),
    INVALID_ENTITY_FOR_UPDATE  (10703, "invalid_entity_for_update", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR           (10704, "validation_error", HttpStatus.BAD_REQUEST),
    CREATE_QUERY_ERROR         (10705, "create_query_error", HttpStatus.BAD_REQUEST),
    UNKNOWN_ERROR              (99999, "unknown_error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final CustomExceptionType customExceptionType;

    SampleExceptionType(int errorCode, String reasonMessage, HttpStatus httpStatus) {
        this.customExceptionType = new CustomExceptionType(httpStatus, errorCode, reasonMessage);
    }

    public static CustomExceptionType of(String reasonMessage){
        return Arrays.stream(SampleExceptionType.values())
                .filter(exceptionType -> Objects.equals(exceptionType.getReasonMessage(), reasonMessage))
                .findFirst()
                .orElse(UNKNOWN_ERROR)
                .getCustomExceptionType();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.customExceptionType.httpStatus;
    }

    @Override
    public int getErrorCode() {
        return this.customExceptionType.errorCode;
    }

    @Override
    public String getReasonMessage() {
        return this.customExceptionType.reasonMessage;
    }

    public CustomExceptionType getCustomExceptionType() {
        return customExceptionType;
    }
}
