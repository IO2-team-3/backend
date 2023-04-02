package com.team3.central.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestInstance(Lifecycle.PER_CLASS)
class OrganizerServiceTest {

  private OrganizerService organizerService;
  @Mock
  private OrganizerRepository organizerRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock
  private ConfirmationTokenService confirmationTokenService;
  @Mock
  private JwtService jwtService;
  @Mock
  private EmailService emailService;

  private static OrganizerEntity organizer;
  private static Set<Event> events;

  @BeforeAll
  void setUp() {
    MockitoAnnotations.openMocks(this);
    organizerService = new OrganizerService(organizerRepository,
        bCryptPasswordEncoder, confirmationTokenService, emailService, jwtService);

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
  void testSignUpWithNewOrganizer() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, "encodedPassword");
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");
    when(organizerRepository.save(any(OrganizerEntity.class))).thenReturn(organizerEntity);

    // when
    ResponseEntity<OrganizerEntity> responseEntity = organizerService.signUp(name, email, password);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertThat(organizerEntity).isEqualToComparingFieldByFieldRecursively(responseEntity.getBody());
  }


  @Test
  void testSignUpWithExistingUnauthorisedOrganizer() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(false);
    final ConfirmationToken confirmationToken = new ConfirmationToken(organizerEntity);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));

    // when
    final ResponseEntity<OrganizerEntity> responseEntity = organizerService.signUp(name, email,
        password);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
  }

  @Test
  void testSignUpOrganizerExistsAndAuthorized() {
    // given
    final String name = "Test Organizer";
    final String email = "test@example.com";
    final String password = "testpassword";

    final OrganizerEntity organizer = new OrganizerEntity(name, email, password);
    organizer.setId(1L);
    organizer.setIsAuthorised(true);

    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizer));

    // when
    ResponseEntity<OrganizerEntity> response = organizerService.signUp(name, email, password);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testConfirmOrganizerNotFound() {
    // given
    final String id = "1";
    final String token = "testtoken";

    when(confirmationTokenService.getToken(token)).thenReturn(Optional.empty());

    // when
    final ResponseEntity<OrganizerEntity> response = organizerService.confirm(id, token);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testLoginOrganizerExists() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final String token = "token123";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(true);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));
    when(jwtService.generateToken(organizerEntity)).thenReturn(token);
    when(bCryptPasswordEncoder.matches(password, organizerEntity.getPassword())).thenReturn(true);

    // when
    final ResponseEntity<String> response = organizerService.login(email, password);

    // then
    assertThat(response).isNotNull()
        .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
        .containsExactly(HttpStatus.OK, token);
  }

  @Test
  void testLoginOrganizerDoesntExist() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final String token = "token123";
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when
    final ResponseEntity<String> response = organizerService.login(email, password);

    // then
    assertThat(response).isNotNull()
        .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
        .containsExactly(HttpStatus.BAD_REQUEST,null);
  }

  @Test
  void testLoginWrongPassword() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final String token = "token123";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(true);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));
    when(jwtService.generateToken(organizerEntity)).thenReturn(token);
    when(bCryptPasswordEncoder.matches(password, organizerEntity.getPassword())).thenReturn(false);

    // when
    final ResponseEntity<String> response = organizerService.login(email, password);

    // then
    assertThat(response).isNotNull()
        .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
        .containsExactly(HttpStatus.BAD_REQUEST,null);
  }

  @Test
  public void deleteOrganizerWhenExists() {
    // given
    Long id = organizer.getId();
    when(organizerRepository.existsById(id)).thenReturn(true);
    when(organizerRepository.findById(id)).thenReturn(Optional.of(organizer));
    when(bCryptPasswordEncoder.encode(organizer.getEmail())).thenReturn("hashedEmail");

    // when
    organizerService.deleteOrganizer(id);

    // then
    verify(organizerRepository).findById(id);
    verify(organizerRepository).save(organizer);
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
    when(organizerRepository.existsById(id)).thenReturn(false);

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

    when(organizerRepository.existsById(id)).thenReturn(true);
    when(organizerRepository.findById(id)).thenReturn(Optional.of(organizer));

    // when
    Set<Event> result = organizerService.getEventsOfOrganizer(id);

    // then
    verify(organizerRepository).findById(id);
    assertThat(events).isEqualTo(result);
  }

  @Test
  public void throwWhenGettingEventsOfNonExistingOrganizer() {
    // given
    Long id = organizer.getId();

    when(organizerRepository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> {
      // when
      organizerService.getEventsOfOrganizer(id);
    })
        // then
        .isInstanceOf(IndexOutOfBoundsException.class)
        .hasMessage("Id does not exist");
  }
}

