package ir.bigz.springbootreal.exception.validation;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ErrorResponseModel {

    private final Map<String, String> errors;
    private final String timestamp;
    private final String path;
}
