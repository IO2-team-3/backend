package com.team3.central.repositories;

import com.team3.central.repositories.entities.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ReservationRepository extends CrudRepository<Reservation, Long> {
    Reservation findById(long id);
}
