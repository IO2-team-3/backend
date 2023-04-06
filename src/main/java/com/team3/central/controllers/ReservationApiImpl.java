package com.team3.central.controllers;

import com.team3.central.openapi.api.ReservationApi;
import com.team3.central.openapi.model.ReservationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationApiImpl implements ReservationApi {

  /**
   * DELETE /reservation : Delete reservation Delete reservation
   *
   * @param reservationToken token of reservation (required)
   * @return deleted (status code 204) or token not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> deleteReservation(
      @RequestHeader(value = "reservationToken", required = true) String reservationToken) {

    return null;
  }

  /**
   * POST /reservation : Create new reservation Create new reservation
   *
   * @param eventId ID of event (required)
   * @param placeID ID of place (optional)
   * @return created (status code 201) or no free place or place taken (status code 400) or event
   * not exist or done (status code 404)
   */
  @Override
  public ResponseEntity<ReservationDTO> makeReservation(
      @RequestHeader(value = "eventId", required = true) Long eventId,
      @RequestHeader(value = "placeID", required = false) Long placeID) {
    return null;
  }

}
