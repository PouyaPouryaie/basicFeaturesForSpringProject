package ir.bigz.springbootreal.validation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidationHandler {

    private final ValidationUtils validationUtils;

    public ValidationHandler(@Qualifier("ValidationUtilsImpl") ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    public boolean apply(ValidationType validation, List<String> allowed, Object value) {

        boolean pass = false;
        switch (validation) {
            case EMAIL:
                pass = validationUtils.isEmailValid(value.toString());
                break;
            case GENDER:
                pass = validationUtils.isGenderValid(value.toString(), allowed);
                break;
            case NATIONAL_CODE:
                pass = validationUtils.isNationalCodeValid(value.toString());
                break;
            case MOBILE:
                pass = validationUtils.isMobileValid(value.toString());
                break;
        }

        return pass;
    }
}
