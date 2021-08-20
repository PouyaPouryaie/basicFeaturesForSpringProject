package ir.bigz.springbootreal.validation;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ValidationUtils {

    boolean isEmailValid(String email);
    boolean isGenderValid(String gender, List<String> allowed);
    boolean isNationalCodeValid(String nationalCode);
    boolean isMobileValid(String mobile);
}
