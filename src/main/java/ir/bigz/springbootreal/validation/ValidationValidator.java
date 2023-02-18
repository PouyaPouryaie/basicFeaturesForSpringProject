package ir.bigz.springbootreal.validation;

import ir.bigz.springbootreal.validation.annotation.Validator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ValidationValidator implements ConstraintValidator<Validator, String> {

    boolean nullOption;
    List<String> allowed;
    ValidationType validationsType;
    private final ValidationHandler validationHandler;
    public ValidationValidator(ValidationHandler validationHandler) {
        this.validationHandler = validationHandler;
    }

    @Override
    public void initialize(Validator constraintAnnotation) {
        validationsType = constraintAnnotation.value();
        nullOption = constraintAnnotation.nullOption();
        if(constraintAnnotation.allowed().length > 0){
            allowed = Arrays.asList(constraintAnnotation.allowed());
        }
        else if(validationsType.getDefaultValue().length > 0){
            allowed = Arrays.asList(validationsType.getDefaultValue());
        }
        else {
            allowed = Collections.emptyList();
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if(!validationHandler.apply(validationsType, allowed, value, nullOption)){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(validationsType.getFailedMessage())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }


}
