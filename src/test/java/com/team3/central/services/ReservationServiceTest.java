import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.EventRepository;
import com.team3.central.repositories.ReservationRepository;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.Reservation;
import com.team3.central.repositories.entities.enums.EventStatus;
import com.team3.central.services.ReservationService;
import com.team3.central.services.exceptions.NoFreePlaceException;
import com.team3.central.services.exceptions.NotFoundException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReservationServiceTest {

  private ReservationRepository reservationRepository;
  private EventRepository eventRepository;
  private ReservationService reservationService;

  private static Stream<Arguments> testData() {
    return Stream.of(
        Arguments.of(EventStatus.DONE),
        Arguments.of(EventStatus.CANCELLED)
    );
  }

  @BeforeEach
  public void setUp() {
    reservationRepository = mock(ReservationRepository.class);
    eventRepository = mock(EventRepository.class);
    reservationService = new ReservationService(reservationRepository, eventRepository);
  }

  @Test
  public void deleteReservationSuccess() throws NotFoundException {
    // given
    String reservationToken = "reservationToken";
    Reservation reservation = new Reservation();
    Event event = new Event();
    event.setFreePlace(5L);
    reservation.setEvent(event);
    reservation.setReservationToken(reservationToken);

    // when
    when(reservationRepository.findByReservationToken(reservationToken))
        .thenReturn(reservation);

    reservationService.deleteReservation(reservationToken);

    // then
    assertNull(reservation.getReservationToken());
  }

  @Test
  public void deleteReservationReservationNotFound() {
    // given
    String reservationToken = "reservationToken";

    // when & then
    when(reservationRepository.findByReservationToken(reservationToken)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> {
      reservationService.deleteReservation(reservationToken);
    });
  }

  @Test
  public void makeReservationWithoutPlaceIdSuccess()
      throws NotFoundException, NoFreePlaceException {
    // given
    Long eventId = 1L;
    Reservation reservation = new Reservation();
    reservation.setEvent(new Event());
    reservation.getEvent().setId(eventId);
    reservation.getEvent().setFreePlace(5L);

    // when
    when(reservationRepository.findFirstByEventIdAndReservationTokenIsNull(eventId))
        .thenReturn(reservation);

    Reservation result = reservationService.makeReservation(eventId, null);

    // then
    assertNotNull(result.getReservationToken());
  }

  @Test
  public void makeReservationWithPlaceIdSuccess() throws NotFoundException, NoFreePlaceException {
    // given
    Long eventId = 1L;
    Long placeId = 3L;

    Event event = new Event();
    event.setId(eventId);
    event.setFreePlace(5L);

    Reservation reservation = new Reservation();
    reservation.setReservationToken(null);
    reservation.setEvent(event);

    // when
    when(reservationRepository.findByEventIdAndPlaceOnSchema(eventId, placeId)).thenReturn(
        reservation);

    Reservation result = reservationService.makeReservation(eventId, placeId);

    // then
    assertNotNull(result.getReservationToken());
  }

  @Test
  public void makeReservationPlaceIdIsNullAndNoMoreFreePlaces() {
    // given
    Long eventId = 1L;
    Long placeId = null;

    // when
    when(reservationRepository.findFirstByEventIdAndReservationTokenIsNull(eventId)).thenReturn(
        null);

    // then
    assertThrows(NotFoundException.class,
        () -> reservationService.makeReservation(eventId, placeId));
  }

  @Test
  public void makeReservationWithAlreadyReservedPlaceId() {
    // given
    Long eventId = 1L;
    Long placeId = 4L;

    Event event = new Event();
    event.setId(eventId);
    event.setFreePlace(5L);

    Reservation reservation = new Reservation();
    reservation.setReservationToken("token");
    reservation.setEvent(event);

    // when & then
    when(reservationRepository.findByEventIdAndPlaceOnSchema(eventId, placeId)).thenReturn(
        reservation);

    assertThrows(NoFreePlaceException.class, () -> {
      reservationService.makeReservation(eventId, placeId);
    });
  }

  @Test
  public void makeReservationWithWrongPlaceId() {
    // given
    Long eventId = 1L;
    Long placeId = 6L;

    Event event = new Event();
    event.setId(eventId);
    event.setFreePlace(5L);

    Reservation reservation = new Reservation();
    reservation.setReservationToken("token");
    reservation.setEvent(event);

    // when & then
    when(reservationRepository.findByEventIdAndPlaceOnSchema(eventId, placeId)).thenReturn(null);

    assertThrows(NotFoundException.class, () -> {
      reservationService.makeReservation(eventId, placeId);
    });
  }

  @ParameterizedTest
  @MethodSource("testData")
  public void makeReservationOnAlreadyFinishedOrCancelledEvent(EventStatus eventStatus) {
    // given
    Long eventId = 1L;
    Long placeId = 3L;

    Event event = new Event();
    event.setId(eventId);
    event.setFreePlace(5L);
    event.setStatus(eventStatus);

    Reservation reservation = new Reservation();
    reservation.setEvent(event);

    // when & then
    when(reservationRepository.findByEventIdAndPlaceOnSchema(eventId, placeId)).thenReturn(
        reservation);

    assertThrows(NotFoundException.class, () -> {
      reservationService.makeReservation(eventId, placeId);
    });
  }
}