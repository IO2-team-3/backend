package com.team3.central.controllers;

import com.team3.central.mappers.OrganizerMapper;
import com.team3.central.openapi.api.OrganizerApi;
import com.team3.central.openapi.model.InlineResponse200;
import com.team3.central.openapi.model.Organizer;
import com.team3.central.openapi.model.OrganizerForm;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.OrganizerService;
import com.team3.central.services.exceptions.AlreadyExistsException;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.NotFoundException;
import com.team3.central.services.exceptions.WrongTokenException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrganizerApiImpl implements OrganizerApi {

  OrganizerService organizerService;

  /**
   * POST /organizer/{id} : Confirm organizer account
   *
   * @param id   id of Organizer (required)
   * @param code code from email (required)
   * @return nothing to do, account already confirmed (status code 200) or account confirmed (status
   * code 202) or code wrong (status code 400) or organizer id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> confirm(String id, String code) {
    HttpStatus status = HttpStatus.ACCEPTED;
    try {
      organizerService.confirm(id, code);
    } catch (Exception exception) {
      if (exception instanceof BadIdentificationException) {
        status = HttpStatus.BAD_REQUEST;
      } else if (exception instanceof WrongTokenException) {
        status = HttpStatus.BAD_REQUEST;
      } else if (exception instanceof AlreadyExistsException) {
        status = HttpStatus.OK;
      } else if (exception instanceof NotFoundException) {
        status = HttpStatus.NOT_FOUND;
      } else {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    return new ResponseEntity<>(status);
  }

  /**
   * DELETE /organizer/{id} : Delete orginizer account
   *
   * @param id id of Organizer (required)
   * @return deleted (status code 204) or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> deleteOrganizer(String id) {
    Set<Event> eventsOfOrganizer;
    try {
      eventsOfOrganizer = organizerService.getEventsOfOrganizer(Long.parseLong(id));
    } catch (IndexOutOfBoundsException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    boolean hasUncompletedEvents = eventsOfOrganizer.stream()
        .anyMatch(e -> e.getStatus() != EventStatus.DONE && e.getStatus() != EventStatus.CANCELLED);
    if (hasUncompletedEvents) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try {
      organizerService.deleteOrganizer(Long.parseLong(id));
    } catch (IndexOutOfBoundsException e) {
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
   * POST /organizer : Create orginizer account
   *
   * @param organizerForm Add event (optional)
   * @return successful operation (status code 201) or email already in use (status code 400)
   */
  @Override
  public ResponseEntity<Organizer> signUp(OrganizerForm organizerForm) {
    var organizerEntityResponseEntity = organizerService.signUp(organizerForm.getName(),
        organizerForm.getEmail(), organizerForm.getPassword());
    if (!organizerEntityResponseEntity.hasBody()) {
      return new ResponseEntity<Organizer>(organizerEntityResponseEntity.getStatusCode());
    }
    OrganizerMapper mapper = new OrganizerMapper();
    var organizerDto = mapper.convertToModel(
        Objects.requireNonNull(organizerEntityResponseEntity.getBody()));

    return new ResponseEntity<>(organizerDto, organizerEntityResponseEntity.getStatusCode());
  }

  /**
   * GET /organizer : Get organizer account (my account)
   *
   * @return successful operation (status code 200) or invalid session (status code 400)
   */
  @Override
  public ResponseEntity<Organizer> getOrganizer() {
    UserDetails userDetails = getUserDetails();
    if (userDetails == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Optional<OrganizerEntity> user = organizerService.getOrganizerFromEmail(
        userDetails.getUsername());
    if (user.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    OrganizerMapper mapper = new OrganizerMapper();
    return new ResponseEntity<>(mapper.convertToModel(user.get()), HttpStatus.OK);
  }

  private UserDetails getUserDetails() {
    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
