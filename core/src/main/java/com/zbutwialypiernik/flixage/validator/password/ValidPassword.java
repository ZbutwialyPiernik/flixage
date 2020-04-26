package com.zbutwialypiernik.flixage.validator.password;

import com.zbutwialypiernik.flixage.validator.password.PasswordConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password does not match requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
