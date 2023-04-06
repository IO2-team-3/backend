package com.team3.central.controllers;

import com.team3.central.openapi.api.ReservationApi;
import com.team3.central.openapi.model.ReservationDTO;
import com.team3.central.repositories.entities.Reservation;
import com.team3.central.services.ReservationService;
import com.team3.central.services.exceptions.NoFreePlaceException;
import com.team3.central.services.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationApiImpl implements ReservationApi {

  private final ReservationService reservationService;

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

    try {
      Reservation reservation = reservationService.makeReservation(eventId, placeID);
      ReservationDTO reservationDTO = new ReservationDTO();
      reservationDTO.setReservationToken(reservation.getReservationToken());
      reservationDTO.setPlaceId(reservation.getPlaceOnSchema());
      reservationDTO.setEventId(reservation.getEvent().getId());
      //TODO: add mapper
      return new ResponseEntity<>(reservationDTO, HttpStatus.CREATED);
    } catch (Exception e) {
      if (e instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (e instanceof NoFreePlaceException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

}
