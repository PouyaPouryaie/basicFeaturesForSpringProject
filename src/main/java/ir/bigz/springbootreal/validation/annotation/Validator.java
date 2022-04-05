package ir.bigz.springbootreal.validation.annotation;

import ir.bigz.springbootreal.validation.ValidationType;
import ir.bigz.springbootreal.validation.ValidationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ValidationValidator.class)
public @interface Validator {

    String message() default "not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] allowed() default {};
    boolean nullOption() default false;
    ValidationType value();
}
