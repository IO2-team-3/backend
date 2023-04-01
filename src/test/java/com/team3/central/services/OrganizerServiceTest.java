package com.team3.central.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.OrganizerEntity;
import java.util.Optional;
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

  @BeforeAll
  void setUp() {
    MockitoAnnotations.openMocks(this);
    organizerService = new OrganizerService(organizerRepository,
        bCryptPasswordEncoder, confirmationTokenService, emailService, jwtService);
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
}

