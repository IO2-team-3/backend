package com.team3.central.validators;

import com.team3.central.openapi.model.EventForm;
import com.team3.central.openapi.model.EventPatch;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventValidator {

  CategoryValidator categoryValidator;

  public void validateEventForm(EventForm eventForm) throws IllegalArgumentException {
    for (var id : eventForm.getCategoriesIds()) {
      if (id == null || id < 1) {
        categoryValidator.validateCategoryId(Long.valueOf(id));
      }
    }
    long endTime = eventForm.getEndTime();
    long startTime = eventForm.getStartTime();
    if (endTime == 0 || startTime == 0) {
      throw new IllegalArgumentException("Start time and end time cannot be null");
    }
    if (endTime < startTime) {
      throw new IllegalArgumentException("End time cannot be before start time");
    }
    if (eventForm.getMaxPlace() == null || eventForm.getMaxPlace() < 1) {
      throw new IllegalArgumentException("Max place cannot be less than 1");
    }
    if (eventForm.getName() == null || eventForm.getName().isBlank()) {
      throw new IllegalArgumentException("Event name cannot be null or empty");
    }
    if (eventForm.getTitle() == null || eventForm.getTitle().isBlank()) {
      throw new IllegalArgumentException("Event title cannot be null or empty");
    }
    if (eventForm.getPlaceSchema() == null || eventForm.getPlaceSchema().isBlank()) {
      throw new IllegalArgumentException("Event placeSchema cannot be null or empty");
    }
    if (eventForm.getLatitude() == null || eventForm.getLatitude().isBlank()
        || eventForm.getLongitude() == null || eventForm.getLongitude().isBlank()) {
      throw new IllegalArgumentException("Event latitude and longitude cannot be null or empty");
    }
    try {
      Double.parseDouble(eventForm.getLatitude());
      Double.parseDouble(eventForm.getLongitude());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Event latitude and longitude must be numbers");
    }
  }

  public void validateEventId(Long eventId) throws IllegalArgumentException {
    if (eventId == null || eventId < 1) {
      throw new IllegalArgumentException("Event id cannot be null or less than 1");
    }
  }

  public void validateEventPatch(EventPatch eventPatch) throws IllegalArgumentException {
    Long endTime = eventPatch.getEndTime();
    Long startTime = eventPatch.getStartTime();

    if (endTime != null && startTime != null && endTime < startTime) {
      throw new IllegalArgumentException("End time cannot be before start time");
    }
    if (eventPatch.getMaxPlace() != null && eventPatch.getMaxPlace() < 1) {
      throw new IllegalArgumentException("Max place cannot be less than 1");
    }
    if (eventPatch.getName() != null && eventPatch.getName().isBlank()) {
      throw new IllegalArgumentException("Event name cannot be null or empty");
    }
    if (eventPatch.getTitle() != null && eventPatch.getTitle().isBlank()) {
      throw new IllegalArgumentException("Event title cannot be null or empty");
    }
    if (eventPatch.getPlaceSchema() != null && eventPatch.getPlaceSchema().isBlank()) {
      throw new IllegalArgumentException("Event placeSchema cannot be null or empty");
    }
    if (eventPatch.getLatitude() != null && eventPatch.getLatitude().isBlank()) {
      throw new IllegalArgumentException("Event latitude cannot be null or empty");
    }
    if (eventPatch.getLongitude() != null && eventPatch.getLongitude().isBlank()) {
      throw new IllegalArgumentException("Event longitude cannot be null or empty");
    }
    try {
      if (eventPatch.getLatitude() != null) {
        Double.parseDouble(eventPatch.getLatitude());
      }
      if (eventPatch.getLongitude() != null) {
        Double.parseDouble(eventPatch.getLongitude());
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Event latitude and longitude must be numbers");
    }

  }
}
