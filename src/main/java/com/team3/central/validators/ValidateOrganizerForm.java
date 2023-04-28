package com.team3.central.validators;

import com.team3.central.openapi.model.OrganizerForm;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ValidateOrganizerForm extends OrganizerForm {
  public ValidateOrganizerForm(OrganizerForm organizerForm) {
    setName(organizerForm.getName());
    setEmail(organizerForm.getEmail());
    setPassword(organizerForm.getPassword());
  }

  @NotBlank(message = "Name is mandatory")
  @Override
  public String getName() {
    return super.getName();
  }

  @Email(message = "Email should be valid")
  @Override
  public String getEmail() {
    return super.getEmail();
  }

  @Size(min = 8, message = "Password should be at least 8 characters long")
  @Override
  public String getPassword() {
    return super.getPassword();
  }
}
