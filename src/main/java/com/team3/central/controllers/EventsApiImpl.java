package com.team3.central.controllers;

import com.team3.central.openapi.api.EventsApi;
import com.team3.central.openapi.model.Event;
import com.team3.central.openapi.model.EventForm;
import com.team3.central.openapi.model.EventPatch;
import com.team3.central.openapi.model.EventWithPlaces;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.services.CategoryService;
import com.team3.central.services.EventService;
import com.team3.central.services.OrganizerService;
import com.team3.central.services.exceptions.NotFoundException;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventsApiImpl implements EventsApi {

  private final EventService eventService;
  private final OrganizerService organizerService;
  private final CategoryService categoryService;

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
  public ResponseEntity<Event> addEvent(
      @ApiParam(value = "Add event") @Valid @RequestBody(required = false) EventForm eventForm) {
    UserDetails userDetails = getUserDetails();
    OrganizerEntity organizer = organizerService.getOrganizerFromEmail(userDetails.getUsername())
        .get();

    // BUG: if token is invalid/expired, it returns 500, not 403
    try {
      Event event = eventService.addEvent(eventForm.getTitle(), eventForm.getName(),
          eventForm.getMaxPlace(), eventForm.getStartTime(), eventForm.getEndTime(),
          eventForm.getLatitude(), eventForm.getLongitude(),
          categoryService.getCategoriesFromIds(eventForm.getCategoriesIds()),
          eventForm.getPlaceSchema(), organizer);
      return new ResponseEntity<>(event, HttpStatus.CREATED);
    } catch (IllegalArgumentException exception) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
   *         or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> cancelEvent(
      @ApiParam(value = "id of Event", required = true) @PathVariable("id") String id) {
    UserDetails userDetails = getUserDetails();
    if (userDetails == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    boolean result = eventService.deleteEvent(Long.parseLong(id), userDetails.getUsername());
    if (result) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * GET /events/getByCategory : Return list of all events in category
   *
   * @param categoryId ID of category (required)
   * @return successful operation (status code 200)
   *         or Invalid category ID supplied (status code 400)
   */
  @Override
  public ResponseEntity<List<Event>> getByCategory(
      @NotNull @ApiParam(value = "ID of category", required = true) @Valid @RequestParam(value = "categoryId", required = true) Long categoryId) {
    // It's assumed, that valid category ID is any integer
    return new ResponseEntity<>(eventService.getEventsByCategory(categoryId), HttpStatus.OK);
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
  public ResponseEntity<EventWithPlaces> getEventById(
      @ApiParam(value = "ID of event to return", required = true) @PathVariable("id") Long id) {
    try {
      Optional<EventWithPlaces> event = eventService.getById(id);
      return new ResponseEntity<>(event.get(),HttpStatus.OK);
    } catch (NotFoundException exception) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * GET /events : Return list of all events
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Event>> getEvents() {
    return new ResponseEntity<>(eventService.getAllEvents(),HttpStatus.OK);
  }

  /**
   *
   * User needs to be authorized
   *
   * GET /events/my : Return list of events made by organizer, according to session
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Event>> getMyEvents() {
    UserDetails userDetails = getUserDetails();
    if(userDetails == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(eventService.getForUser(userDetails.getUsername()), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> patchEvent(
      @ApiParam(value = "id of Event", required = true) @PathVariable("id") String id,
      @ApiParam(value = "Update an existent user in the store") @Valid @RequestBody(required = false) EventPatch eventPatch) {
    UserDetails userDetails = getUserDetails();
    if (userDetails == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    try {
      eventService.patchEvent(Long.valueOf(id), userDetails.getUsername(), eventPatch);
    } catch (NotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private UserDetails getUserDetails() {
    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
