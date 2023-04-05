package com.team3.central.repositories;

import com.team3.central.repositories.entities.Event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  Event findById(long id);

  List<Event> findAll();
  //byCategory
}