package com.team3.central.validators;

import com.team3.central.validators.exceptions.CustomValidationException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class DTOValidator {

  private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private static final Validator validator = factory.getValidator();

  public static <T> void validate(T dto) {
    Set<ConstraintViolation<T>> violations = validator.validate(dto);
    if (!violations.isEmpty()) {
      // Handle validation errors
      // You can throw a custom exception here, which will be caught by the global exception handler
      throw new CustomValidationException(violations);
    }
  }
}
