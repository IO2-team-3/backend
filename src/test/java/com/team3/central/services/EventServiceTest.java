package com.team3.central.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.team3.central.openapi.model.EventPatch;
import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.EventRepository;
import com.team3.central.repositories.ReservationRepository;
import com.team3.central.repositories.entities.Category;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.Reservation;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.exceptions.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class EventServiceTest {

  private static EventRepository eventRepository;
  private static ReservationRepository reservationRepository;
  private static CategoryRepository categoryRepository;
  private static EventService eventService;

  @BeforeAll
  static void setUp() {
    eventRepository = Mockito.mock(EventRepository.class);
    categoryRepository = Mockito.mock(CategoryRepository.class);
    reservationRepository = Mockito.mock(ReservationRepository.class);
    eventService = new EventService(eventRepository, reservationRepository, categoryRepository);
  }

  private static Stream<Arguments> testData() {
    return Stream.of(
        Arguments.of(EventStatus.CANCELLED),
        Arguments.of(EventStatus.PENDING),
        Arguments.of(EventStatus.DONE)
    );
  }

  @Test
  void addEvent() {
    // given
    String title = "test title";
    String name = "test name";
    Long freePlace = 10L;
    Long startTime = 100L;
    Long endTime = 2000L;
    String latitude = "12";
    String longitude = "23";
    Set<Category> categories = Set.of();
    String placeSchema = "some place schema in base 64";
    OrganizerEntity organizer = new OrganizerEntity("name", "mail@email.com", "password");
    when(eventRepository.save(any())).thenReturn(new Event());
    when(reservationRepository.save(any())).thenReturn(new Reservation());
    // when
    com.team3.central.openapi.model.Event result = eventService.addEvent(
        title,
        name,
        freePlace,
        startTime,
        endTime,
        latitude,
        longitude,
        categories,
        placeSchema,
        organizer
    );
    // then
    assertThat(result).extracting("title", "name", "freePlace", "startTime", "endTime", "latitude",
            "longitude", "categories", "placeSchema")
        .containsExactly(title, name, freePlace, startTime,
            endTime, latitude, longitude, List.of(), placeSchema);
  }

  @SneakyThrows
  @Test
  void getByExistingId() {
    // given
    final Long id = 1L;
    final Long maxPlaces = 12L;
    final String title = "test title";
    final String name = "test name";
    final OrganizerEntity organizer = new OrganizerEntity("someName", "some@mail.com", "password");
    final String placeSchema = "test place schema";
    final String latitude = "12";
    final String longitude = "-12";
    final Long startTime = 123L;
    final Long endTime = 321L;
    final Event event = Event.builder()
        .id(id)
        .title(title)
        .name(name)
        .maxPlace(maxPlaces)
        .reservations(Set.of())
        .categories(Set.of())
        .organizer(organizer)
        .status(EventStatus.INFUTURE)
        .placeSchema(placeSchema)
        .freePlace(maxPlaces)
        .longitude(longitude)
        .latitude(latitude)
        .endTime(endTime)
        .startTime(startTime)
        .build();

    when(eventRepository.existsById(1L)).thenReturn(true);
    when(eventRepository.findById(id)).thenReturn(Optional.of(event));

    // when
    Optional<com.team3.central.openapi.model.EventWithPlaces> result = eventService.getById(id);

    // then
    assertThat(result).isPresent().hasValueSatisfying(
        r -> assertThat(r).extracting("title", "name", "freePlace", "startTime", "endTime",
                "latitude",
                "longitude", "categories", "placeSchema")
            .containsExactly(title, name, maxPlaces, startTime,
                endTime, latitude, longitude, List.of(), placeSchema));
  }

  @Test
  void getByNonExistingId() {
    // given
    final Long id = 2L;
    when(eventRepository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> {
      // when
      eventService.getById(id);
    })
        // then
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Index does not exist");
  }

  @Test
  void getAllEvents() {
    // given
    final Long id = 1L;
    final Long maxPlaces = 12L;
    final String title = "test title";
    final String name = "test name";
    final OrganizerEntity organizer = new OrganizerEntity("someName", "some@mail.com", "password");
    final String placeSchema = "test place schema";
    final String latitude = "12";
    final String longitude = "-12";
    final Long startTime = 123L;
    final Long endTime = 321L;
    final Event event = Event.builder()
        .id(id)
        .title(title)
        .name(name)
        .maxPlace(maxPlaces)
        .reservations(Set.of())
        .categories(Set.of())
        .organizer(organizer)
        .status(EventStatus.INFUTURE)
        .placeSchema(placeSchema)
        .freePlace(maxPlaces)
        .longitude(longitude)
        .latitude(latitude)
        .endTime(endTime)
        .startTime(startTime)
        .build();
    when(eventRepository.findAll()).thenReturn(List.of(event));

    // when
    List<com.team3.central.openapi.model.Event> res = eventService.getAllEvents();

    // then
    assertThat(res).isNotEmpty();
  }

  @Test
  void getEventsByCategory() {
    final Long id = 1L;
    final Long maxPlaces = 12L;
    final String title = "test title";
    final String name = "test name";
    final OrganizerEntity organizer = new OrganizerEntity("someName", "some@mail.com", "password");
    final String placeSchema = "test place schema";
    final String latitude = "12";
    final String longitude = "-12";
    final Long startTime = 123L;
    final Long endTime = 321L;
    final Category category = new Category();
    category.setId(1L);
    category.setName("category name");
    final Event event = Event.builder()
        .id(id)
        .title(title)
        .name(name)
        .maxPlace(maxPlaces)
        .reservations(Set.of())
        .categories(Set.of(category))
        .organizer(organizer)
        .status(EventStatus.INFUTURE)
        .placeSchema(placeSchema)
        .freePlace(maxPlaces)
        .longitude(longitude)
        .latitude(latitude)
        .endTime(endTime)
        .startTime(startTime)
        .build();
    when(eventRepository.findAll()).thenReturn(List.of(event));

    // when
    List<com.team3.central.openapi.model.Event> res = eventService.getEventsByCategory(1L);

    // then
    assertThat(res).isNotEmpty();
  }

  @Test
  void getForUser() {
    final Long id = 1L;
    final Long maxPlaces = 12L;
    final String title = "test title";
    final String name = "test name";
    final Long organizerId = 21L;
    final OrganizerEntity organizer = new OrganizerEntity("someName", "some@mail.com", "password");
    organizer.setId(organizerId);
    final String placeSchema = "test place schema";
    final String latitude = "12";
    final String longitude = "-12";
    final Long startTime = 123L;
    final Long endTime = 321L;
    final Event event = Event.builder()
        .id(id)
        .title(title)
        .name(name)
        .maxPlace(maxPlaces)
        .reservations(Set.of())
        .categories(Set.of())
        .organizer(organizer)
        .status(EventStatus.INFUTURE)
        .placeSchema(placeSchema)
        .freePlace(maxPlaces)
        .longitude(longitude)
        .latitude(latitude)
        .endTime(endTime)
        .startTime(startTime)
        .build();
    when(eventRepository.findAll()).thenReturn(List.of(event));

    // when
    List<com.team3.central.openapi.model.Event> res = eventService.getForUser("some@mail.com");

    // then
    assertThat(res).isNotEmpty();
  }

  @Test
  public void deleteEventEventNotFound() {
    // given
    final String ORGANIZER_EMAIL = "organizer@example.com";
    final Long EVENT_ID = 1L;
    when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when
    boolean result = eventService.deleteEvent(EVENT_ID, ORGANIZER_EMAIL);

    // then
    assertFalse(result);
  }

  @Test
  public void deleteEventOrganizerEmailMismatch() {
    // given
    final String ORGANIZER_EMAIL = "organizer@example.com";
    final Long EVENT_ID = 1L;
    Event event = new Event();
    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail("other@example.com");
    event.setOrganizer(organizer);
    when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

    // when
    boolean result = eventService.deleteEvent(EVENT_ID, ORGANIZER_EMAIL);

    // then
    assertFalse(result);
  }

  @ParameterizedTest
  @MethodSource("testData")
  public void deleteEventEventAlreadyCancelled(EventStatus status) {
    // given
    final String ORGANIZER_EMAIL = "organizer@example.com";
    final Long EVENT_ID = 1L;
    Event event = new Event();
    event.setStatus(status);
    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(ORGANIZER_EMAIL);
    event.setOrganizer(organizer);
    when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

    // when
    boolean result = eventService.deleteEvent(EVENT_ID, ORGANIZER_EMAIL);

    // then
    assertFalse(result);
  }

  @Test
  public void deleteEventValidRequest() {
    // given
    final String ORGANIZER_EMAIL = "organizer@example.com";
    final Long EVENT_ID = 1L;
    Event event = new Event();
    event.setStatus(EventStatus.INFUTURE);
    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(ORGANIZER_EMAIL);
    event.setOrganizer(organizer);
    when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
    when(eventRepository.save(any(Event.class))).thenReturn(event);

    // when
    boolean result = eventService.deleteEvent(EVENT_ID, ORGANIZER_EMAIL);

    // then
    assertTrue(result);
    assertEquals(EventStatus.CANCELLED, event.getStatus());
  }

  @Test
  public void deleteEventInvlalidId() {
    // given
    final String ORGANIZER_EMAIL = "organizer@example.com";
    final Long EVENT_ID = -1L;
    Event event = new Event();
    event.setStatus(EventStatus.INFUTURE);
    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(ORGANIZER_EMAIL);
    event.setOrganizer(organizer);
    when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(eventRepository.save(any(Event.class))).thenReturn(event);

    // when
    boolean result = eventService.deleteEvent(EVENT_ID, ORGANIZER_EMAIL);

    // then
    assertFalse(result);
  }

  @Test
  public void patchEventValid() throws NotFoundException {
    // given
    Long eventId = 1L;
    String organizerEmail = "organizer@example.com";
    EventPatch eventPatch = new EventPatch()
        .title("New Event Title")
        .name("New Event Name")
        .startTime(1700000000L)
        .endTime(1710000000L)
        .latitude("40.1234")
        .longitude("-73.5678")
        .placeSchema("Serialized Place Schema")
        .maxPlace(50L)
        .addCategoriesIdsItem(1)
        .addCategoriesIdsItem(2);

    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(organizerEmail);

    Event event = Event.builder()
        .id(eventId)
        .title("Old Event Title")
        .name("Old Event Name")
        .startTime(1600000000L)
        .endTime(1610000000L)
        .latitude("40.5678")
        .longitude("-73.1234")
        .placeSchema("Old Serialized Place Schema")
        .maxPlace(100L)
        .categories(new HashSet<>())
        .organizer(organizer)
        .status(EventStatus.INFUTURE)
        .build();

    Optional<Event> optionalEvent = Optional.of(event);
    when(eventRepository.findById(eventId)).thenReturn(optionalEvent);
    when(categoryRepository.findById(1L)).thenReturn(new Category());
    when(categoryRepository.findById(2L)).thenReturn(new Category());

    // when
    eventService.patchEvent(eventId, organizerEmail, eventPatch);
  }

  @Test
  public void patchEventWithInvalidEventId() {
    // given
    Long eventId = 1L;
    String organizerEmail = "organizer@example.com";
    EventPatch eventPatch = new EventPatch();

    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> eventService.patchEvent(eventId, organizerEmail, eventPatch))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Event does not exist");
  }

  @Test
  public void patchEventWithInvalidOrganizerEmail() {
    // given
    Long eventId = 1L;
    String organizerEmail = "organizer@example.com";
    String otherOrganizerEmail = "other.organizer@example.com";
    EventPatch eventPatch = new EventPatch();

    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(otherOrganizerEmail);

    Event event = Event.builder()
        .id(eventId)
        .organizer(organizer)
        .build();

    Optional<Event> optionalEvent = Optional.of(event);

    when(eventRepository.findById(eventId)).thenReturn(optionalEvent);

    // when & then
    assertThatThrownBy(() -> eventService.patchEvent(eventId, organizerEmail, eventPatch))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("You are not organizer of this event");
  }

  @Test
  public void patchEventWithNullEventPatch() {
    // given
    Long eventId = 1L;
    String organizerEmail = "organizer@example.com";
    EventPatch eventPatch = null;

    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(organizerEmail);

    Event event = Event.builder()
        .id(eventId)
        .organizer(organizer)
        .status(EventStatus.INFUTURE)
        .build();

    Optional<Event> optionalEvent = Optional.of(event);

    when(eventRepository.findById(eventId)).thenReturn(optionalEvent);

    // when & then
    assertThatThrownBy(() -> eventService.patchEvent(eventId, organizerEmail, eventPatch))
        .isInstanceOf(NullPointerException.class);
  }

  @ParameterizedTest
  @MethodSource("testData")
  public void patchEventEventNotInFuture(EventStatus status) throws NotFoundException {
    // given
    Long eventId = 1L;
    String organizerEmail = "organizer@example.com";
    EventPatch eventPatch = new EventPatch()
        .title("New Event Title")
        .name("New Event Name")
        .startTime(1700000000L)
        .endTime(1710000000L)
        .latitude("40.1234")
        .longitude("-73.5678")
        .placeSchema("Serialized Place Schema")
        .maxPlace(50L)
        .addCategoriesIdsItem(1)
        .addCategoriesIdsItem(2);

    OrganizerEntity organizer = new OrganizerEntity();
    organizer.setEmail(organizerEmail);

    Event event = Event.builder()
        .id(eventId)
        .title("Old Event Title")
        .name("Old Event Name")
        .startTime(1600000000L)
        .endTime(1610000000L)
        .latitude("40.5678")
        .longitude("-73.1234")
        .placeSchema("Old Serialized Place Schema")
        .maxPlace(100L)
        .categories(new HashSet<>())
        .organizer(organizer).status(status)
        .build();

    Optional<Event> optionalEvent = Optional.of(event);

    when(eventRepository.findById(eventId)).thenReturn(optionalEvent);

    // when & then
    assertThatThrownBy(() -> eventService.patchEvent(eventId, organizerEmail, eventPatch))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Event is not in future");
  }

}