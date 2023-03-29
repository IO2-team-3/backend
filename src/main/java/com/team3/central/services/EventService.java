package com.team3.central.services;


import com.team3.central.mappers.EventMapper;
import com.team3.central.repositories.EventRepository;
import com.team3.central.repositories.entities.Category;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.EventStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
  private final EventRepository eventRepository;
  private final EventMapper eventMapper;

  @Autowired
  public EventService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
    this.eventMapper = new EventMapper();}

  public com.team3.central.openapi.model.Event addEvent(String title, String name, Integer freePlace,
      Integer startTime, Integer endTime, String latitude,
      String longitude, Set<Category> categories, String placeSchema, OrganizerEntity organizer) {
    Event event = new Event(
        title,
        name,
        startTime.longValue(),
        endTime.longValue(),
        latitude,
        longitude,
        freePlace.longValue(),
        placeSchema,
        EventStatus.INFUTURE,
        organizer,
        Set.of(),
        categories
    );

    eventRepository.save(event);
    return eventMapper.convertToModel(event);
  }

  public Optional<com.team3.central.openapi.model.Event> getById(Long id) throws IndexOutOfBoundsException {
    if(!eventRepository.existsById(id)) throw new IndexOutOfBoundsException("Index does not exist");
    return eventRepository.findById(id).map(eventMapper::convertToModel);
  }
  public List<com.team3.central.openapi.model.Event> getAllEvents() {
    return eventRepository.findAll()
        .stream()
        .map(eventMapper::convertToModel)
        .collect(Collectors.toList());
  }

  public List<com.team3.central.openapi.model.Event> getEventsByCategory(Long categoryId) {
    return eventRepository.findAll()
        .stream()
        .filter(event -> event.getCategories()
            .stream()
            .map(category -> categoryId)
            .collect(Collectors.toList())
            .contains(categoryId))
        .map(eventMapper::convertToModel)
        .collect(Collectors.toList());
  }

  public List<com.team3.central.openapi.model.Event> getForUser(String email) {
    return eventRepository.findAll()
        .stream()
        .filter(event -> event.getOrganizer().getEmail().equals(email))
        .map(eventMapper::convertToModel)
        .collect(Collectors.toList());
  }
}
