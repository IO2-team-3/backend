package com.team3.central.controllers;

import com.team3.central.openapi.api.EventsApi;
import com.team3.central.openapi.model.Event;
import com.team3.central.openapi.model.EventForm;
import com.team3.central.openapi.model.EventPatch;
import com.team3.central.openapi.model.EventWithPlaces;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.services.CategoriesService;
import com.team3.central.services.EventService;
import com.team3.central.services.OrganizerService;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.EventNotChangedException;
import com.team3.central.services.exceptions.NoCategoryException;
import com.team3.central.services.exceptions.NotFoundException;
import com.team3.central.validators.CategoryValidator;
import com.team3.central.validators.EventValidator;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventsApiImpl implements EventsApi {

  private final EventService eventService;
  private final OrganizerService organizerService;
  private final CategoriesService categoryService;
  private final EventValidator eventValidator;
  private final CategoryValidator categoryValidator;
  /**
   *
   * User needs to be authenticated
   *
   * POST /events : Add new event
   *
   * @param eventForm Add event (optional)
   * @return event created (status code 201)
   *         or event can not be created, field invalid (status code 400)
   *         or invalid session (status code 403)
   */
  @Override
  public ResponseEntity<Event> addEvent(EventForm eventForm) {
    try {
      UserDetails userDetails = getUserDetails();
      OrganizerEntity organizer = organizerService.getOrganizerFromEmail(userDetails.getUsername())
          .get();
      eventValidator.validateEventForm(eventForm);
      Event event = eventService.addEvent(eventForm.getTitle(), eventForm.getName(),
          eventForm.getMaxPlace(), eventForm.getStartTime(), eventForm.getEndTime(),
          eventForm.getLatitude(), eventForm.getLongitude(),
          categoryService.getCategoriesFromIds(eventForm.getCategoriesIds()),
          eventForm.getPlaceSchema(), organizer);
      return new ResponseEntity<>(event, HttpStatus.CREATED);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   *
   * User needs to be authorized
   *
   * DELETE /events/{id} : Cancel event
   *
   * @param id id of Event (required)
   * @return deleted (status code 204)
   *         or invalid session (status code 403)
   *         or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> cancelEvent(String id) {
    try {
      eventValidator.validateEventId(Long.parseLong(id));
      UserDetails userDetails = getUserDetails();
      eventService.deleteEvent(Long.parseLong(id), userDetails.getUsername());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (e instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * GET /events/getByCategory : Return list of all events in category
   *
   * @param categoryId ID of category (required)
   * @return successful operation (status code 200) or Invalid category ID supplied (status code
   * 400)
   */
  @Override
  public ResponseEntity<List<Event>> getByCategory(Long categoryId) {
    try {
      categoryValidator.validateCategoryId(categoryId);
      return new ResponseEntity<>(eventService.getEventsByCategory(categoryId), HttpStatus.OK);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * GET /events/{id} : Find event by ID
   * Returns a single event
   *
   * @param id ID of event to return (required)
   * @return successful operation (status code 200)
   *         or Invalid ID supplied (status code 400)
   *         or Event not found (status code 404)
   */
  @Override
  public ResponseEntity<EventWithPlaces> getEventById(Long id) {
    try {
      eventValidator.validateEventId(id);
      Optional<EventWithPlaces> event = eventService.getById(id);
      return new ResponseEntity<>(event.get(), HttpStatus.OK);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (e instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  /**
   * GET /events : Return list of all events
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Event>> getEvents() {
    return new ResponseEntity<>(eventService.getAllEvents(), HttpStatus.OK);
  }

  /**
   *
   * User needs to be authorized
   *
   * GET /events/my : Return list of events made by organizer, according to session
   *
   * @return successful operation (status code 200)
   *         or invalid session (status code 403)
   */
  @Override
  public ResponseEntity<List<Event>> getMyEvents() {
    UserDetails userDetails = null;
    try {
      userDetails = getUserDetails();
      return new ResponseEntity<>(eventService.getForUser(userDetails.getUsername()),
          HttpStatus.OK);
    } catch (BadIdentificationException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /**
   * PATCH /events/{id} : patch existing event
   *
   * @param id id of Event (required)
   * @param eventPatch Update an existent user in the store (optional)
   * @return nothing to do, no field to patch (status code 200)
   *         or patched (status code 202)
   *         or invalid id or fields in body (status code 400)
   *         or invalid session (status code 403)
   *         or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> patchEvent(String id, EventPatch eventPatch) {
    try {
      UserDetails userDetails = getUserDetails();
      eventValidator.validateEventId(Long.valueOf(id));
      eventValidator.validateEventPatch(eventPatch);
      eventService.patchEvent(Long.valueOf(id), userDetails.getUsername(), eventPatch);
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof NotFoundException) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else if (e instanceof EventNotChangedException) {
        return new ResponseEntity<>(HttpStatus.OK);
      } else if (e instanceof NoCategoryException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else if (e instanceof BadIdentificationException) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      } else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
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
