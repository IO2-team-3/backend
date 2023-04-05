package com.team3.central.mappers;

import com.team3.central.openapi.model.EventStatus;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventStatusMapper {

  public com.team3.central.repositories.entities.enums.EventStatus convertToEntity(
      EventStatus eventStatus) {
    switch (eventStatus) {
      case DONE:
        return com.team3.central.repositories.entities.enums.EventStatus.DONE;
      case INFUTURE:
        return com.team3.central.repositories.entities.enums.EventStatus.INFUTURE;
      case PENDING:
        return com.team3.central.repositories.entities.enums.EventStatus.PENDING;
    }
    return com.team3.central.repositories.entities.enums.EventStatus.CANCELLED;
  }

  public EventStatus convertToModel(
      com.team3.central.repositories.entities.enums.EventStatus eventStatus) {
    switch (eventStatus) {
      case DONE:
        return EventStatus.DONE;
      case INFUTURE:
        return EventStatus.INFUTURE;
      case PENDING:
        return EventStatus.PENDING;
    }
    return EventStatus.CANCELLED;
  }
}
