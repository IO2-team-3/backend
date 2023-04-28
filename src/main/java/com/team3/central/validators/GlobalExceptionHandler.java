package com.team3.central.validators;

import com.team3.central.validators.exceptions.CustomValidationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomValidationException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(CustomValidationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getViolations().forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
