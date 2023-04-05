package com.team3.central.mappers;

import com.team3.central.openapi.model.Organizer;
import com.team3.central.openapi.model.Organizer.StatusEnum;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;

public class OrganizerMapper {

  public Organizer convertToModel (OrganizerEntity organizerEntity) {
    Organizer organizerModel = new Organizer();
    organizerModel.setId(organizerEntity.getId());
    organizerModel.setName(organizerEntity.getName());
    organizerModel.setEmail(organizerEntity.getEmail());
    organizerModel.setStatus(organizerEntity.isAuthorized() ? StatusEnum.CONFIRMED : StatusEnum.PENDING);
    return organizerModel;
  }

  public OrganizerEntity convertToEntity(Organizer organizer) {
    OrganizerEntity organizerEntity = new OrganizerEntity();
    organizerEntity.setName(organizer.getName());
    organizerEntity.setEmail(organizer.getEmail());
    return organizerEntity;
  }

}
