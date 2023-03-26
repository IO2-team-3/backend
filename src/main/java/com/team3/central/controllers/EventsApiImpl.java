package com.team3.central.controllers;

import com.team3.central.openapi.api.ApiUtil;
import com.team3.central.openapi.api.EventsApi;
import com.team3.central.openapi.model.Event;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventsApiImpl implements EventsApi {

  /**
   * POST /events : Add new event
   *
   * @param title title of Event (required)
   * @param name title of Event (required)
   * @param freePlace No of free places (required)
   * @param startTime Unix time stamp of begin of event (required)
   * @param endTime Unix time stamp of end of event (required)
   * @param latitude Latitude of event (required)
   * @param longitude Longitude of event (required)
   * @param categories Unix time stamp of end of event (required)
   * @param placeSchema seralized place schema (optional)
   * @return event created (status code 201)
   *         or event can not be created (status code 400)
   */
  @Override
  public ResponseEntity<Event> addEvent(
      @NotNull @ApiParam(value = "title of Event", required = true) @Valid @RequestParam(value = "title", required = true) String title,
      @NotNull @ApiParam(value = "title of Event", required = true) @Valid @RequestParam(value = "name", required = true) String name,
      @NotNull @ApiParam(value = "No of free places", required = true) @Valid @RequestParam(value = "freePlace", required = true) Integer freePlace,
      @NotNull @ApiParam(value = "Unix time stamp of begin of event", required = true) @Valid @RequestParam(value = "startTime", required = true) Integer startTime,
      @NotNull @ApiParam(value = "Unix time stamp of end of event", required = true) @Valid @RequestParam(value = "endTime", required = true) Integer endTime,
      @NotNull @ApiParam(value = "Latitude of event", required = true) @Valid @RequestParam(value = "latitude", required = true) String latitude,
      @NotNull @ApiParam(value = "Longitude of event", required = true) @Valid @RequestParam(value = "longitude", required = true) String longitude,
      @NotNull @ApiParam(value = "Unix time stamp of end of event", required = true) @Valid @RequestParam(value = "categories", required = true) List<Integer> categories,
      @ApiParam(value = "seralized place schema") @Valid @RequestParam(value = "placeSchema", required = false) String placeSchema) {
    getRequest().ifPresent(request -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"latitude\" : \"40.4775315\", \"name\" : \"Long description of Event\", \"freePlace\" : 2, \"startTime\" : 1673034164, \"id\" : 10, \"endTime\" : 1683034164, \"categories\" : [ { \"name\" : \"Sport\", \"id\" : 1 }, { \"name\" : \"Sport\", \"id\" : 1 } ], \"title\" : \"Short description of Event\", \"longitude\" : \"-3.7051359\", \"placeSchema\" : \"Seralized place schema\", \"status\" : \"done\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  /**
   * DELETE /events/{id} : Cancel event
   *
   * @param id id of Event (required)
   * @return deleted (status code 204)
   *         or id not found (status code 404)
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
   * @return successful operation (status code 200)
   *         or Invalid category ID supplied (status code 400)
   */
  @Override
  public ResponseEntity<List<Event>> getByCategory(
      @NotNull @ApiParam(value = "ID of category", required = true) @Valid @RequestParam(value = "categoryId", required = true) Long categoryId) {
    getRequest().ifPresent(request -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"latitude\" : \"40.4775315\", \"name\" : \"Long description of Event\", \"freePlace\" : 2, \"startTime\" : 1673034164, \"id\" : 10, \"endTime\" : 1683034164, \"categories\" : [ { \"name\" : \"Sport\", \"id\" : 1 }, { \"name\" : \"Sport\", \"id\" : 1 } ], \"title\" : \"Short description of Event\", \"longitude\" : \"-3.7051359\", \"placeSchema\" : \"Seralized place schema\", \"status\" : \"done\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
  public ResponseEntity<Event> getEventById(
      @ApiParam(value = "ID of event to return", required = true) @PathVariable("id") Long id) {
    getRequest().ifPresent(request -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"latitude\" : \"40.4775315\", \"name\" : \"Long description of Event\", \"freePlace\" : 2, \"startTime\" : 1673034164, \"id\" : 10, \"endTime\" : 1683034164, \"categories\" : [ { \"name\" : \"Sport\", \"id\" : 1 }, { \"name\" : \"Sport\", \"id\" : 1 } ], \"title\" : \"Short description of Event\", \"longitude\" : \"-3.7051359\", \"placeSchema\" : \"Seralized place schema\", \"status\" : \"done\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /events : Return list of all events
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Event>> getEvents() {
    getRequest().ifPresent(request -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"latitude\" : \"40.4775315\", \"name\" : \"Long description of Event\", \"freePlace\" : 2, \"startTime\" : 1673034164, \"id\" : 10, \"endTime\" : 1683034164, \"categories\" : [ { \"name\" : \"Sport\", \"id\" : 1 }, { \"name\" : \"Sport\", \"id\" : 1 } ], \"title\" : \"Short description of Event\", \"longitude\" : \"-3.7051359\", \"placeSchema\" : \"Seralized place schema\", \"status\" : \"done\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * GET /events/my : Return list of events made by organizer, according to session
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Event>> getMyEvents() {
    getRequest().ifPresent(request -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"latitude\" : \"40.4775315\", \"name\" : \"Long description of Event\", \"freePlace\" : 2, \"startTime\" : 1673034164, \"id\" : 10, \"endTime\" : 1683034164, \"categories\" : [ { \"name\" : \"Sport\", \"id\" : 1 }, { \"name\" : \"Sport\", \"id\" : 1 } ], \"title\" : \"Short description of Event\", \"longitude\" : \"-3.7051359\", \"placeSchema\" : \"Seralized place schema\", \"status\" : \"done\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * PATCH /events/{id} : patch existing event
   *
   * @param id id of Event (required)
   * @param event Update an existent user in the store (optional)
   * @return patched (status code 202)
   *         or id not found (status code 404)
   */
  @Override
  public ResponseEntity<Void> patchEvent(
      @ApiParam(value = "id of Event", required = true) @PathVariable("id") String id,
      @ApiParam(value = "Update an existent user in the store") @Valid @RequestBody(required = false) Event event) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

}
