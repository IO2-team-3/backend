package com.team3.central.validators;


import com.team3.central.openapi.model.OrganizerForm;
import com.team3.central.openapi.model.OrganizerPatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class OrganizerValidator {

  public void validateId(String id) throws IllegalArgumentException {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Id is null or empty");
    }
  }

  public void validateCode(String code) throws IllegalArgumentException {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("Confirmation code is null or empty");
    }
  }

  public void validateEmailAndPassword(String email, String password)
      throws IllegalArgumentException {
    if (email == null || !isValidEmail(email)) {
      throw new IllegalArgumentException("Email is null or invalid");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is null or empty");
    }
  }

  private boolean isValidEmail(String email) {
    String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";
    Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(email);
    return matcher.find();
  }

  public void validateOrganizerForm(OrganizerForm organizerForm) throws IllegalArgumentException {
    if (organizerForm == null) {
      throw new IllegalArgumentException("OrganizerForm is null");
    }
    if (organizerForm.getEmail() == null || organizerForm.getEmail().isBlank() || !isValidEmail(
        organizerForm.getEmail())) {
      throw new IllegalArgumentException("Email is null or invalid");
    }
    if (organizerForm.getPassword() == null || organizerForm.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password is null or empty");
    }
    if (organizerForm.getName() == null || organizerForm.getName().isBlank()) {
      throw new IllegalArgumentException("Name is null or empty");
    }
  }

  public void validateOrganizerPatch(OrganizerPatch organizerPatch)
      throws IllegalArgumentException {
    if (organizerPatch == null) {
      throw new IllegalArgumentException("OrganizerPatch is null");
    }
    if (organizerPatch.getName() != null && organizerPatch.getName().isBlank()) {
      throw new IllegalArgumentException("Name is null or empty");
    }
    if (organizerPatch.getPassword() != null && organizerPatch.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password is null or empty");
    }
  }
}
