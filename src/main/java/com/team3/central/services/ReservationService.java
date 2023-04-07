package com.team3.central.services;

import com.team3.central.repositories.EventRepository;
import com.team3.central.repositories.ReservationRepository;
import com.team3.central.repositories.entities.Reservation;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.exceptions.NoFreePlaceException;
import com.team3.central.services.exceptions.NotFoundException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final EventRepository eventRepository;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository,
      EventRepository eventRepository) {
    this.reservationRepository = reservationRepository;
    this.eventRepository = eventRepository;
  }

  public void deleteReservation(String reservationToken) {
//    reservationRepository.deleteByToken(reservationToken);
  }

  public Reservation makeReservation(Long eventId, Long placeId)
      throws NotFoundException, NoFreePlaceException {

    Reservation reservation = reservationRepository.findAll().stream()
        .filter(reservation1 -> reservation1.getEvent().getId().equals(eventId)
            && reservation1.getPlaceOnSchema().equals(placeId))
        .findFirst()
        .orElseThrow(() -> new NotFoundException("No such place in event or such event"));

    if (reservation.getEvent().getStatus() == EventStatus.DONE) {
      throw new NotFoundException("Event is done");
    }
    if (reservation.getReservationToken() != null) {
      throw new NoFreePlaceException("Place is already reserved");
    }

    reservation.setReservationToken(UUID.randomUUID().toString());
    reservationRepository.save(reservation);
    return reservation;
  }

}
