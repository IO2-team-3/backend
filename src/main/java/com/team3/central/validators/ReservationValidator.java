package com.team3.central.validators;

import org.springframework.stereotype.Component;

@Component
public class ReservationValidator {

  public void validateReservationToken(String token) throws IllegalArgumentException {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Token is empty");
    }
  }
  public void validatePlaceId(Long placeId) throws IllegalArgumentException {
    if (placeId != null && placeId < 0) {
      throw new IllegalArgumentException("Place id is invalid");
    }
  }
}
