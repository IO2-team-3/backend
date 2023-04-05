package com.team3.central.controllers;

import com.team3.central.openapi.api.EventsApi;
import com.team3.central.openapi.model.Event;
import com.team3.central.openapi.model.EventForm;
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
   * User needs to be authenticated
   * <p>
   * POST /events : Add new event
   *
   * @param eventForm Add event (optional)
   * @return event created (status code 201) or event can not be created, field invalid (status code
   * 400) or invalid session (status code 403)
   */
  @Override
  public ResponseEntity<Event> addEvent(
      @ApiParam(value = "Add event") @Valid @RequestBody(required = false) EventForm eventForm) {
    UserDetails userDetails = getUserDetails();
    OrganizerEntity organizer = organizerService.getOrganizerFromEmail(userDetails.getUsername())
        .get();
    Event event = eventService.addEvent(eventForm.getTitle(), eventForm.getName(),
        eventForm.getMaxPlace(), eventForm.getStartTime(), eventForm.getEndTime(),
        eventForm.getLatitude(), eventForm.getLongitude(),
        categoryService.getCategoriesFromIds(eventForm.getCategoriesIds()),
        eventForm.getPlaceSchema(), organizer);
    return new ResponseEntity<>(event, HttpStatus.OK);
  }

  /**
   * User needs to be authorized
   * <p>
   * DELETE /events/{id} : Cancel event
   *
   * @param id id of Event (required)
   * @return deleted (status code 204) or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> cancelEvent(
      @ApiParam(value = "id of Event", required = true) @PathVariable("id") String id) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /events/getByCategory : Return list of all events in category
   *
   * @param categoryId ID of category (required)
   * @return successful operation (status code 200) or Invalid category ID supplied (status code
   * 400)
   */
  @Override
  public ResponseEntity<List<Event>> getByCategory(
      @NotNull @ApiParam(value = "ID of category", required = true) @Valid @RequestParam(value = "categoryId", required = true) Long categoryId) {
    return new ResponseEntity<>(eventService.getEventsByCategory(categoryId), HttpStatus.OK);
  }

  /**
   * GET /events/{id} : Find event by ID Returns a single event
   *
   * @param id ID of event to return (required)
   * @return successful operation (status code 200) or Invalid ID supplied (status code 400) or
   * Event not found (status code 404)
   */
  @Override
  public ResponseEntity<EventWithPlaces> getEventById(
      @ApiParam(value = "ID of event to return", required = true) @PathVariable("id") Long id) {
    try {
      Optional<EventWithPlaces> event = eventService.getById(id);
      if (event.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<>(event.get(), HttpStatus.OK);
      }
    } catch (NotFoundException exception) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
   * User needs to be authorized
   * <p>
   * GET /events/my : Return list of events made by organizer, according to session
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Event>> getMyEvents() {
    UserDetails userDetails = getUserDetails();
    if (userDetails == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(eventService.getForUser(userDetails.getUsername()), HttpStatus.OK);
  }

  private UserDetails getUserDetails() {
    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
