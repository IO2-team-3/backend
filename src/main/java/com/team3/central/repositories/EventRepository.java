package com.team3.central.repositories;

import com.team3.central.repositories.entities.Event;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EventRepository extends CrudRepository<Event, Long> {

  Event findById(long id);

  List<Event> findAll();
  //byCategory
}