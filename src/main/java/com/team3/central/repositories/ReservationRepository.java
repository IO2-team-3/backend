package com.team3.central.repositories;

import com.team3.central.repositories.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Reservation findById(long id);
}
