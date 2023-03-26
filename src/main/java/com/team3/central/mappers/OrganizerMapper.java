package com.team3.central.mappers;

import com.team3.central.openapi.model.Organizer;
import com.team3.central.openapi.model.Organizer.StatusEnum;
import com.team3.central.repositories.entities.OrganizerEntity;

public class OrganizerMapper {

  public static Organizer convertToDto(OrganizerEntity organizerEntity) {
    com.team3.central.openapi.model.Organizer organizerDto = new com.team3.central.openapi.model.Organizer();
    organizerDto.setEmail(organizerEntity.getEmail());
    organizerDto.setId(organizerEntity.getId());
    organizerDto.setName(organizerEntity.getName());
    organizerDto.setPassword(organizerEntity.getPassword());
//    organizerDto.setEvents(organizerEntity.getEvents()); TODO: convert entity event to model event
    organizerDto.setStatus(
        organizerEntity.getIsAuthorised() ? StatusEnum.CONFIRMED : StatusEnum.PENDING);
    return organizerDto;
  }

  static private OrganizerEntity convertToEntity(Organizer organizerDto) {
    OrganizerEntity organizerEntity = new OrganizerEntity();
    organizerEntity.setEmail(organizerDto.getEmail());
    organizerEntity.setId(organizerDto.getId());
    organizerEntity.setName(organizerDto.getEmail());
    organizerEntity.setPassword(organizerDto.getEmail());
//    organizerEntity.setEvents(organizerDto.getEvents()); TODO: convert entity event to model event
    organizerEntity.setIsAuthorised(
        organizerDto.getStatus() == StatusEnum.CONFIRMED);
    return organizerEntity;
  }
}
