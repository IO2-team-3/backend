package com.team3.central.validators.exceptions;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class CustomValidationException extends RuntimeException {
  private final Set<ConstraintViolation<?>> violations;

  public CustomValidationException(Set<? extends ConstraintViolation<?>> violations) {
    super("Validation failed");
    this.violations = Set.copyOf(violations);
  }

  public Set<ConstraintViolation<?>> getViolations() {
    return violations;
  }
}