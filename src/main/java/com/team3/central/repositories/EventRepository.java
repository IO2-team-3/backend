package com.team3.central.repositories;
import com.team3.central.repositories.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Override
    Optional<Event> findById(Long id);

    List<Event> findAll();
    //byCategory
}