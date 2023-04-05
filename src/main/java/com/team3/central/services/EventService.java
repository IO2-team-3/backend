package com.team3.central.services;


import com.team3.central.mappers.EventMapper;
import com.team3.central.repositories.EventRepository;
import com.team3.central.repositories.entities.Category;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.exceptions.NotFoundException;
import java.util.HashMap;
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
    this.eventMapper = new EventMapper();
  }

  public com.team3.central.openapi.model.Event addEvent(String title, String name, Long freePlace,
      Long startTime, Long endTime, String latitude,
      String longitude, Set<Category> categories, String placeSchema, OrganizerEntity organizer) {
    Event event = Event.builder()
        .title(title)
        .name(name)
        .startTime(startTime)
        .endTime(endTime)
        .latitude(latitude)
        .longitude(longitude)
        .freePlace(freePlace)
        .placeSchema(placeSchema)
        .status(EventStatus.INFUTURE)
        .organizer(organizer)
        .categories(categories)
        .reservations(Set.of())
        .places(new HashMap<>(freePlace.intValue()))
        .maxPlace(freePlace)
        .build();

    eventRepository.save(event);
    return eventMapper.convertToModel(event);
  }

  public Optional<com.team3.central.openapi.model.EventWithPlaces> getById(Long id) throws NotFoundException {
    if(!eventRepository.existsById(id)) throw new NotFoundException("Index does not exist");
    return eventRepository.findById(id).map(eventMapper::convertToEventWithPlaces);
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
            .map(Category::getId)
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

  public boolean deleteEvent(Long id, String email) {
    Event event = eventRepository.findById(id).orElse(null);
    if (event == null) {
      return false;
    }
    if (!event.getOrganizer().getEmail().equals(email)) {
      return false;
    }
    if (event.getStatus() != EventStatus.INFUTURE) {
      return false;
    }
    event.setStatus(EventStatus.CANCELLED);
    eventRepository.save(event);
    return true;
  }
}
