package com.team3.central.mappers;

import com.team3.central.openapi.model.EventStatus;
import com.team3.central.repositories.entities.Event;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventStatusMapper {
  public com.team3.central.repositories.entities.enums.EventStatus convertToEntity(EventStatus eventStatus) {
    switch (eventStatus) {
      case DONE: return com.team3.central.repositories.entities.enums.EventStatus.DONE;
      case INFUTURE: return com.team3.central.repositories.entities.enums.EventStatus.INFUTURE;
      case PENDING: return com.team3.central.repositories.entities.enums.EventStatus.PENDING;
    }
    return com.team3.central.repositories.entities.enums.EventStatus.CANCELLED;
  }

  public EventStatus convertToModel(Event event, Long currentTime) {
    if (event.getStatus() == com.team3.central.repositories.entities.enums.EventStatus.CANCELLED) return EventStatus.CANCELLED;
    if ( currentTime >= event.getStartTime() && currentTime <= event.getEndTime()) return EventStatus.PENDING;
    if (currentTime > event.getEndTime()) return EventStatus.DONE;
    return EventStatus.INFUTURE;
  }
}
