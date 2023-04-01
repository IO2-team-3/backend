package com.team3.central.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

public class OrganizerServiceTest {
  private static OrganizerRepository mockOrganizerRepository;
  private static BCryptPasswordEncoder mockBCryptPasswordEncoder;
  private static OrganizerService organizerService;
  private static OrganizerEntity organizer;
  private static Set<Event> events;

  @BeforeEach
  public void setUp() {
    // init mocks
    mockOrganizerRepository = Mockito.mock(OrganizerRepository.class);
    mockBCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    ConfirmationTokenService mockConfirmationTokenService = Mockito.mock(
        ConfirmationTokenService.class);
    EmailService mockEmailService = Mockito.mock(EmailService.class);
    JwtService mockJwtService = Mockito.mock(JwtService.class);

    // subject
    organizerService = new OrganizerService(mockOrganizerRepository, mockBCryptPasswordEncoder,
        mockConfirmationTokenService, mockEmailService, mockJwtService);

    organizer = new OrganizerEntity();
    organizer.setId(1L);
    organizer.setName("Test Organizer");
    organizer.setEmail("test@example.com");
    organizer.setStatus(OrganizerStatus.AUTHORIZED);

    Event event1 = new Event();
    event1.setId(1L);
    event1.setName("Test Event 1");
    event1.setOrganizer(organizer);

    Event event2 = new Event();
    event2.setId(2L);
    event2.setName("Test Event 2");
    event2.setOrganizer(organizer);

    events = new HashSet<>();
    events.add(event1);
    events.add(event2);
    organizer.setEvents(events);
  }

  @Test
  public void deleteOrganizerWhenExists() {
    // given
    Long id = organizer.getId();
    when(mockOrganizerRepository.existsById(id)).thenReturn(true);
    when(mockOrganizerRepository.findById(id)).thenReturn(Optional.of(organizer));
    when(mockBCryptPasswordEncoder.encode(organizer.getEmail())).thenReturn("hashedEmail");

    // when
    organizerService.deleteOrganizer(id);

    // then
    verify(mockOrganizerRepository).findById(id);
    verify(mockOrganizerRepository).save(organizer);
    assertThat(organizer)
        .extracting(OrganizerEntity::getStatus)
        .isEqualTo(OrganizerStatus.DELETED);
    assertThat(organizer)
        .extracting(OrganizerEntity::getEmail)
        .isEqualTo("hashedEmail");
  }

  @Test
  public void throwWhenDeletingNonExistingOrganizer() {
    // given
    Long id = organizer.getId();
    when(mockOrganizerRepository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> {
      // when
      organizerService.deleteOrganizer(id);
    })
    // then
        .isInstanceOf(IndexOutOfBoundsException.class)
        .hasMessage("Id does not exist");
  }

  @Test
  public void getEventsOfOrganizerWhenOrganizerExists() {
    // given
    Long id = organizer.getId();

    when(mockOrganizerRepository.existsById(id)).thenReturn(true);
    when(mockOrganizerRepository.findById(id)).thenReturn(Optional.of(organizer));

    // when
    Set<Event> result = organizerService.getEventsOfOrganizer(id);

    // then
    verify(mockOrganizerRepository).findById(id);
    assertThat(events).isEqualTo(result);
  }

  @Test
  public void throwWhenGettingEventsOfNonExistingOrganizer() {
    // given
    Long id = organizer.getId();

    when(mockOrganizerRepository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> {
      // when
      organizerService.getEventsOfOrganizer(id);
    })
    // then
        .isInstanceOf(IndexOutOfBoundsException.class)
        .hasMessage("Id does not exist");
  }
}
