package com.team3.central.validators;

import com.team3.central.openapi.model.OrganizerForm;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidationAspect {

  @Before("execution(* com.team3.central.openapi.api.*.*(..))")
  public void validateDTOs(JoinPoint joinPoint) {
    for (Object arg : joinPoint.getArgs()) {
      if (arg instanceof OrganizerForm) { // Check for the correct class type
        DTOValidator.validate(new ValidateOrganizerForm((OrganizerForm) arg));
      }
    }
  }
}