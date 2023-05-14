package com.team3.central.mappers;

import com.team3.central.openapi.model.Event;
import com.team3.central.openapi.model.EventWithPlaces;
import com.team3.central.openapi.model.Place;
import java.util.Comparator;
import java.util.Date;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EventMapper {

  private final CategoryMapper categoryMapper;
  private final EventStatusMapper eventStatusMapper;

  public EventMapper() {
    this.eventStatusMapper = new EventStatusMapper();
    this.categoryMapper = new CategoryMapper();
  }

  public Event convertToModel(com.team3.central.repositories.entities.Event event) {
    Long currentTime = System.currentTimeMillis() / 1000L;
    Event eventModel = new Event();
    eventModel.setId(event.getId());
    eventModel.setCategories(event.getCategories()
        .stream()
        .map(categoryMapper::convertToModel)
        .collect(Collectors.toList()));
    eventModel.setLatitude(event.getLatitude());
    eventModel.setLongitude(event.getLongitude());
    eventModel.setStartTime(event.getStartTime());
    eventModel.setEndTime(event.getEndTime());
    eventModel.setMaxPlace(event.getMaxPlace());
    eventModel.setFreePlace(event.getFreePlace());
    eventModel.setName(event.getName());
    eventModel.setStatus(eventStatusMapper.convertToModel(event, currentTime));
    eventModel.setTitle(event.getTitle());
    return eventModel;
  }


  public EventWithPlaces convertToEventWithPlaces(
      com.team3.central.repositories.entities.Event event) {
    Long currentTime = System.currentTimeMillis() / 1000L;
    EventWithPlaces eventModel = new EventWithPlaces();
    eventModel.setId(event.getId());
    eventModel.setFreePlace(event.getFreePlace());
    eventModel.setMaxPlace(event.getMaxPlace());
    eventModel.setTitle(event.getTitle());
    eventModel.setStartTime(event.getStartTime());
    eventModel.setEndTime(event.getEndTime());
    eventModel.setLongitude(event.getLongitude());
    eventModel.setLatitude(event.getLatitude());
    eventModel.setName(event.getName());
    eventModel.setPlaceSchema(event.getPlaceSchema());
    eventModel.setStatus(eventStatusMapper.convertToModel(event, currentTime));
    eventModel.setCategories(event.getCategories()
        .stream()
        .map(categoryMapper::convertToModel)
        .collect(Collectors.toList()));
    eventModel.setPlaces(event.getReservations().stream().map(reservation -> {
      Place place = new Place();
      place.setId(reservation.getPlaceOnSchema());
      place.setFree(reservation.getReservationToken()
          == null); // If reservation token is null, then place is free
      return place;
    }).sorted(Comparator.comparing(Place::getId)).collect(Collectors.toList()));

    return eventModel;
  }

  private Place createPlaceFromEntry(Entry<Long, Boolean> entry) {
    Place place = new Place();
    place.setId(entry.getKey());
    place.setFree(entry.getValue());
    return place;
  }
}
