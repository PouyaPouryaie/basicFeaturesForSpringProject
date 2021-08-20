package ir.bigz.springbootreal.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Component("ValidationUtilsImpl")
public class ValidationUtilsImpl implements ValidationUtils{

    private final Pattern Email_Pattern = Pattern.compile("^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@" +
            "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$");

    private final Pattern Mobile_Pattern =  Pattern.compile("^(09|989)\\d{9}$");

    @Override
    public boolean isEmailValid(String email) {
        return Email_Pattern.matcher(email).matches();
    }

    @Override
    public boolean isGenderValid(String gender, List<String> allowed) {
        return allowed.contains(gender.toLowerCase());
    }

    @Override
    public boolean isNationalCodeValid(String nationalCode) {
        boolean isValid = false;
        if (nationalCode != null && nationalCode.length() == 10) {
            if (StringUtils.isNumeric(nationalCode)) {
                AtomicInteger index = new AtomicInteger();
                int controllerConditionNumber = nationalCode.substring(0, 9).chars()
                        .map(i -> Integer.valueOf(Character.toString((char) i)) * (10 - index.getAndIncrement())).sum() % 11;
                int controllerNumber = Integer.valueOf(nationalCode.substring(9));
                isValid = (controllerConditionNumber < 2) ?
                        controllerConditionNumber == controllerNumber :
                        controllerNumber == (11 - controllerConditionNumber);
            }
        }
        return isValid;
    }

    @Override
    public boolean isMobileValid(String mobile) {
        return Mobile_Pattern.matcher(mobile).matches();
    }
}
