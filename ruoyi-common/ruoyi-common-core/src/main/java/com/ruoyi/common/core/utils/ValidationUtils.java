package com.ruoyi.common.core.utils;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidationUtils {
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

    public static void validate(Object object, Class<?> group){
        Set<ConstraintViolation<Object>> validateResult = validator.validate(object, group);
        if(!validateResult.isEmpty()){
            throw new ConstraintViolationException(validateResult);
        }
    }
}