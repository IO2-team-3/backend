package com.team3.central.services;


import com.team3.central.mappers.EventMapper;
import com.team3.central.openapi.model.EventPatch;
import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.EventRepository;
import com.team3.central.repositories.entities.Category;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private final EventRepository eventRepository;
  private final CategoryRepository categoryRepository;
  private final EventMapper eventMapper;

  @Autowired
  public EventService(EventRepository eventRepository, CategoryRepository categoryRepository) {
    this.eventRepository = eventRepository;
    this.eventMapper = new EventMapper();
    this.categoryRepository = categoryRepository;
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

  public Optional<com.team3.central.openapi.model.EventWithPlaces> getById(Long id)
      throws NotFoundException {
    if (!eventRepository.existsById(id)) {
      throw new NotFoundException("Index does not exist");
    }
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

  public void patchEvent(Long id, String email, EventPatch eventPatch) throws NotFoundException {
    var event = eventRepository.findById(id);
    if (event.isEmpty()) {
      throw new NotFoundException("Event does not exist");
    }
    if (!event.get().getOrganizer().getEmail().equals(email)) {
      throw new NotFoundException("You are not organizer of this event");
    }
    if (event.get().getStatus() != EventStatus.INFUTURE) {
      throw new NotFoundException("Event is not in future");
    }

    if (eventPatch.getMaxPlace() != null) {
      event.get().setMaxPlace(eventPatch.getMaxPlace());
    }
    if (eventPatch.getStartTime() != null) {
      event.get().setStartTime(eventPatch.getStartTime());
    }
    if (eventPatch.getTitle() != null) {
      event.get().setTitle(eventPatch.getTitle());
    }
    if (eventPatch.getName() != null) {
      event.get().setName(eventPatch.getName());
    }
    if (eventPatch.getEndTime() != null) {
      event.get().setEndTime(eventPatch.getEndTime());
    }
    if (eventPatch.getPlaceSchema() != null) {
      event.get().setPlaceSchema(eventPatch.getPlaceSchema());
    }
    if (eventPatch.getCategoriesIds() != null) {
      Set<Category> set = new HashSet<>();
      for (Integer integer : eventPatch.getCategoriesIds()) {
        Category category = categoryRepository.findById(integer.longValue());
        if (category == null) {
          throw new NotFoundException("Category does not exist");
        }
        set.add(category);
      }
      event.get().setCategories(set);
    }
    eventRepository.save(event.get());
  }
    List<com.team3.central.openapi.model.Event> list = new ArrayList<>();
    for (Event event : eventRepository.findAll()) {
      if (event.getOrganizer().getEmail().equals(email)) {
        com.team3.central.openapi.model.Event convertToModel = eventMapper.convertToModel(event);
        list.add(convertToModel);
      }
    }
    return list;
  }

}
