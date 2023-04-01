package com.team3.central.controllers;

import com.team3.central.mappers.OrganizerMapper;
import com.team3.central.openapi.api.OrganizerApi;
import com.team3.central.openapi.model.InlineResponse200;
import com.team3.central.openapi.model.Organizer;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.OrganizerService;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrganizerApiImpl implements OrganizerApi {

  OrganizerService organizerService;

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
      return new ResponseEntity<Organizer>(organizerEntityResponseEntity.getStatusCode());
    }
    OrganizerMapper mapper = new OrganizerMapper();
    var organizerDto = mapper.convertToEntity(
        Objects.requireNonNull(organizerEntityResponseEntity.getBody()));
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
    Set<Event> eventsOfOrganizer;
    try {
      eventsOfOrganizer = organizerService.getEventsOfOrganizer(Long.parseLong(id));
    } catch(IndexOutOfBoundsException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    boolean hasUncompletedEvents = eventsOfOrganizer.stream()
        .anyMatch(e -> e.getStatus() != EventStatus.DONE && e.getStatus() != EventStatus.CANCELLED);
    if(hasUncompletedEvents) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try {
      organizerService.deleteOrganizer(Long.parseLong(id));
    } catch(IndexOutOfBoundsException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    var res = organizerService.login(email, password);
    if (res.hasBody()) {
      InlineResponse200 inlineResponse200 = new InlineResponse200();
      inlineResponse200.sessionToken(Objects.requireNonNull(res.getBody()));
      return new ResponseEntity<InlineResponse200>(inlineResponse200, res.getStatusCode());
    } else {
      return new ResponseEntity<InlineResponse200>(res.getStatusCode());
    }
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
    OrganizerMapper mapper = new OrganizerMapper();
    var organizerDto = mapper.convertToEntity(
        Objects.requireNonNull(organizerEntityResponseEntity.getBody()));

    return new ResponseEntity<>(organizerDto, organizerEntityResponseEntity.getStatusCode());
  }


}
