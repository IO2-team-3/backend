package com.team3.central.repositories;
import com.team3.central.repositories.entities.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface EventRepository extends CrudRepository<Event, Long>{
    Event findById(long id);
    List<Event> findAll();
    //byCategory
}