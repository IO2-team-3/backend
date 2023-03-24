package com.team3.central.controllers;

import com.team3.central.openapi.api.OrganizerApi;
import com.team3.central.openapi.model.InlineResponse200;
import com.team3.central.openapi.model.Organizer;
import com.team3.central.openapi.model.Organizer.StatusEnum;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.services.OrganizerService;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

@RestController
@AllArgsConstructor
public class OrganizerApiImpl implements OrganizerApi {

  OrganizerService organizerService;

  /**
   * @return
   */
  @Override
  public Optional<NativeWebRequest> getRequest() {
    return OrganizerApi.super.getRequest();
  }

  /**
   * POST /organizer/{id} : Confirm orginizer account
   *
   * @param id   id of Organizer (required)
   * @param code code from email (required)
   * @return account confirmed (status code 201) or code wrong (status code 400)
   */
  @Override
  public ResponseEntity<Organizer> confirm(String id, String code) {
    var organizerEntityResponseEntity = organizerService.confirm(id, code);
    if (!organizerEntityResponseEntity.hasBody()) {
//      organizerEntityResponseEntity.se
      return new ResponseEntity<Organizer>(organizerEntityResponseEntity.getStatusCode());
    }
    var organizerDto = convertToDto(organizerEntityResponseEntity.getBody());
    return new ResponseEntity<>(organizerDto, organizerEntityResponseEntity.getStatusCode());
  }

  /**
   * DELETE /organizer/{id} : Confirm orginizer account
   *
   * @param id id of Organizer (required)
   * @return deleted (status code 204) or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> deleteOrganizer(String id) {
    return OrganizerApi.super.deleteOrganizer(id);
  }

  /**
   * GET /organizer/login : Logs organizer into the system
   *
   * @param email    The organizer email for login (required)
   * @param password the password (required)
   * @return successful operation (status code 200) or Invalid email/password supplied (status code
   * 400)
   */
  @Override
  public ResponseEntity<InlineResponse200> loginOrganizer(String email, String password) {
    return OrganizerApi.super.loginOrganizer(email, password);
  }

  /**
   * PATCH /organizer/{id} : Patch orginizer account
   *
   * @param id        id of Organizer (required)
   * @param organizer Update an existent user in the store (optional)
   * @return patched (status code 202) or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> patchOrganizer(String id, Organizer organizer) {
    return OrganizerApi.super.patchOrganizer(id, organizer);
  }

  /**
   * POST /organizer : Create orginizer account
   *
   * @param name     name of Organizer (required)
   * @param email    email of Organizer (required)
   * @param password password of Organizer (required)
   * @return successful operation (status code 201) or organizer already exist (status code 400)
   */
  @Override
  public ResponseEntity<Organizer> signUp(String name, String email, String password) {
    var organizerEntityResponseEntity = organizerService.signUp(name, email, password);
    if (!organizerEntityResponseEntity.hasBody()) {
      return new ResponseEntity<Organizer>(organizerEntityResponseEntity.getStatusCode());
    }
    var organizerDto = convertToDto(organizerEntityResponseEntity.getBody());

    return new ResponseEntity<>(organizerDto, organizerEntityResponseEntity.getStatusCode());
  }

  private Organizer convertToDto(OrganizerEntity organizerEntity) {
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

  private OrganizerEntity convertToEntity(Organizer organizerDto) {
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
