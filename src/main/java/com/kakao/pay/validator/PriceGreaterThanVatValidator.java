package com.kakao.pay.validator;

import com.kakao.pay.request.PriceRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PriceGreaterThanVatValidator implements ConstraintValidator<PriceGreaterThanVat, PriceRequest> {
    @Override
    public void initialize(PriceGreaterThanVat constraintAnnotation) {
    }

    @Override
    public boolean isValid(PriceRequest value, ConstraintValidatorContext context) {
        if (value.getPrice() != null && value.getVat() != null) {
            return value.getPrice() > value.getVat();
        } else {
            return true;
        }
    }
}