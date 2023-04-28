package com.team3.central.controllers;

import com.team3.central.mappers.OrganizerMapper;
import com.team3.central.openapi.api.OrganizerApi;
import com.team3.central.openapi.model.InlineResponse200;
import com.team3.central.openapi.model.Organizer;
import com.team3.central.openapi.model.OrganizerForm;
import com.team3.central.openapi.model.OrganizerPatch;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.OrganizerService;
import com.team3.central.services.exceptions.AlreadyExistsException;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.NotFoundException;
import com.team3.central.services.exceptions.OrganizerStillHasActiveEventsException;
import com.team3.central.services.exceptions.WrongTokenException;
import com.team3.central.validators.OrganizerValidator;
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
  OrganizerValidator organizerValidator;
  /**
   * POST /organizer/{id} : Confirm organizer account
   *
   * @param id id of Organizer (required)
   * @param code code from email (required)
   * @return nothing to do, account already confirmed (status code 200)
   *         or account confirmed (status code 202)
   *         or code wrong (status code 400)
   *         or organizer id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> confirm(String id, String code) {
    HttpStatus status = HttpStatus.ACCEPTED;
    try {
      organizerValidator.validateId(id);
      organizerValidator.validateCode(code);
      organizerService.confirm(id, code);

      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    } catch (Exception exception) {
      if (exception instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (exception instanceof WrongTokenException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (exception instanceof AlreadyExistsException) {
        return new ResponseEntity<>(HttpStatus.OK);
      } else if (exception instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (exception instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * DELETE /organizer/{id} : Delete orginizer account
   *
   * @param id id of Organizer (required)
   * @return deleted (status code 204) or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> deleteOrganizer(String id) {
    try {
      organizerValidator.validateId(id);
      Set<Event> eventsOfOrganizer = organizerService.getEventsOfOrganizer(Long.parseLong(id));
      boolean hasUncompletedEvents = eventsOfOrganizer.stream().anyMatch(
          e -> e.getStatus() != EventStatus.DONE && e.getStatus() != EventStatus.CANCELLED);
      if (hasUncompletedEvents) {
        throw new OrganizerStillHasActiveEventsException("Organizer still has active events");
      }
      organizerService.deleteOrganizer(Long.parseLong(id));

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      if (e instanceof IndexOutOfBoundsException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (e instanceof OrganizerStillHasActiveEventsException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
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
    try {
      organizerValidator.validateEmailAndPassword(email, password);
      String token = organizerService.login(email, password);
      InlineResponse200 inlineResponse200 = new InlineResponse200();
      inlineResponse200.sessionToken(token);
      return new ResponseEntity<>(inlineResponse200, HttpStatus.OK);
    } catch (Exception e) {
      if (e instanceof NotFoundException || e instanceof BadIdentificationException
          || e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }


  /**
   * POST /organizer : Create orginizer account
   *
   * @param organizerForm Add event (optional)
   * @return successful operation (status code 201)
   *         or email already in use (status code 400)
   */
  @Override
  public ResponseEntity<Organizer> signUp(OrganizerForm organizerForm) {
    try {
      organizerValidator.validateOrganizerForm(organizerForm);
      OrganizerEntity entity = organizerService.signUp(organizerForm.getName(),
          organizerForm.getEmail(), organizerForm.getPassword());
      OrganizerMapper mapper = new OrganizerMapper();
      return new ResponseEntity<>(mapper.convertToModel(entity), HttpStatus.CREATED);
    } catch (Exception e) {
      if (e instanceof AlreadyExistsException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * GET /organizer : Get organizer account (my account)
   *
   * @return successful operation (status code 200) or invalid session (status code 400)
   */
  @Override
  public ResponseEntity<Organizer> getOrganizer() {
    try {
      UserDetails userDetails = getUserDetails();
      OrganizerEntity user = organizerService.getOrganizerFromEmail(userDetails.getUsername());
      OrganizerMapper mapper = new OrganizerMapper();

      return new ResponseEntity<>(mapper.convertToModel(user), HttpStatus.OK);
    } catch (Exception e) {
      if (e instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * PATCH /organizer/{id} : Patch orginizer account
   *
   * @param id             id of Organizer (required)
   * @param organizerPatch Add event (optional)
   * @return nothing to do, no field to patch (status code 200) or patched (status code 202) or
   * invalid email or password (status code 400) or invalid session (status code 403) or id not
   * found (status code 404)
   */
  @Override
  public ResponseEntity<Void> patchOrganizer(String id, OrganizerPatch organizerPatch) {
    try {
      organizerValidator.validateId(id);
      organizerValidator.validateOrganizerPatch(organizerPatch);
      OrganizerEntity organizerEntity = getOrganizerEntity();
      if (organizerEntity.getId() != Long.parseLong(id)) {
        throw new NotFoundException("Wrong id");
      }
      organizerService.patchOrganizer(Long.parseLong(id), organizerPatch);

      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    } catch (Exception exception) {
      if (exception instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (exception instanceof IndexOutOfBoundsException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (exception instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      } else if (exception instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  private OrganizerEntity getOrganizerEntity()
      throws NotFoundException, BadIdentificationException {
    UserDetails userDetails = getUserDetails();

    return organizerService.getOrganizerFromEmail(userDetails.getUsername());
  }

  private UserDetails getUserDetails() throws BadIdentificationException {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    if (userDetails == null) {
      throw new BadIdentificationException("User not found");
    }
    return userDetails;
  }

}
