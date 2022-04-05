package ir.bigz.springbootreal.validation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ValidationHandler {

    private final ValidationUtils validationUtils;

    public ValidationHandler(@Qualifier("ValidationUtilsImpl") ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    public boolean apply(ValidationType validation, List<String> allowed, Object value, boolean nullOption) {

        boolean pass = false;

        if(nullOption && Objects.isNull(value)){
            return true;
        }
        else if(!nullOption && Objects.isNull(value)){
            return false;
        }
        else{
            switch (validation) {
                case EMAIL:
                    pass = validationUtils.isEmailValid((String) value);
                    break;
                case GENDER:
                    pass = validationUtils.isGenderValid((String) value, allowed);
                    break;
                case NATIONAL_CODE:
                    pass = validationUtils.isNationalCodeValid((String) value);
                    break;
                case MOBILE:
                    pass = validationUtils.isMobileValid((String) value);
                    break;
            }
        }

        return pass;
    }
}
