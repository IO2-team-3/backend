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
    var exists = eventRepository.findById(eventId).isPresent();
    if (!exists) {
      throw new NotFoundException("Event not exist");
    }
    var event = eventRepository.findById(eventId).get();
    if (event.getStatus() == EventStatus.DONE) {
      throw new NotFoundException("Event is done");
    }
    if (event.getMaxPlace() - event.getFreePlace() <= 0) {
      throw new NoFreePlaceException("No free places");
    }
    if (event.getPlaces().get(placeId)) {
      throw new NoFreePlaceException("Place is taken");
    }
    event.getPlaces().replace(placeId, true);
    eventRepository.save(event);

    Reservation reservation = new Reservation(event, placeId, UUID.randomUUID().toString());
    reservationRepository.save(reservation);
    return reservation;
  }


}
