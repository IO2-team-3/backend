package com.team3.central.mappers;

import com.team3.central.openapi.model.ReservationDTO;
import com.team3.central.repositories.entities.Reservation;

public class ReservationMapper {

  public ReservationDTO convertToModel(Reservation reservation) {
    ReservationDTO reservationDTO = new ReservationDTO();
    reservationDTO.setPlaceId(reservation.getPlaceOnSchema());
    reservationDTO.setEventId(reservation.getEvent().getId());
    reservationDTO.setReservationToken(reservation.getReservationToken());
    return reservationDTO;
  }

}
