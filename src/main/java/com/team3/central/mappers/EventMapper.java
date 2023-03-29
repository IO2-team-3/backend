package com.team3.central.mappers;

import com.team3.central.openapi.model.Event;
import java.util.Set;
import java.util.stream.Collectors;

public class EventMapper {

  private final CategoryMapper categoryMapper;
  private final EventStatusMapper eventStatusMapper;

  public EventMapper() {
    this.eventStatusMapper = new EventStatusMapper();
    this.categoryMapper = new CategoryMapper();
  }

  public com.team3.central.repositories.entities.Event convertToDto(Event event) {
    com.team3.central.repositories.entities.Event eventDTO = new com.team3.central.repositories.entities.Event();
    eventDTO.setId(event.getId());
    eventDTO.setFreePlace(event.getFreePlace());
    eventDTO.setTitle(event.getTitle());
    eventDTO.setStartTime(event.getStartTime());
    eventDTO.setEndTime(event.getEndTime());
    eventDTO.setLongitude(event.getLongitude());
    eventDTO.setLatitude(event.getLatitude());
    eventDTO.setName(event.getName());
    eventDTO.setPlaceSchema(event.getPlaceSchema());
    eventDTO.setStatus(eventStatusMapper.convertToEntity(event.getStatus()));
    eventDTO.setCategories(Set.copyOf(
        event.getCategories()
            .stream()
            .map(categoryMapper::convertToEntity)
            .collect(Collectors.toList())));
    return eventDTO;
  }
  public Event convertToModel(com.team3.central.repositories.entities.Event event) {
    Event eventModel = new Event();
    eventModel.setId(event.getId());
    eventModel.setFreePlace(event.getFreePlace());
    eventModel.setTitle(event.getTitle());
    eventModel.setStartTime(event.getStartTime());
    eventModel.setEndTime(event.getEndTime());
    eventModel.setLongitude(event.getLongitude());
    eventModel.setLatitude(event.getLatitude());
    eventModel.setName(event.getName());
    eventModel.setPlaceSchema(event.getPlaceSchema());
    eventModel.setStatus(eventStatusMapper.convertToModel(event.getStatus()));
    eventModel.setCategories(event.getCategories()
        .stream().map(categoryMapper::convertToModel)
        .collect(Collectors.toList()));
    return eventModel;
  }
}
