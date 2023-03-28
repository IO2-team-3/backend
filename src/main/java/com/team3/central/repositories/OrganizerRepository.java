package com.team3.central.repositories;

import com.team3.central.repositories.entities.Organizer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface OrganizerRepository extends CrudRepository<Organizer, Long> {

  Organizer findById(long id);
}
