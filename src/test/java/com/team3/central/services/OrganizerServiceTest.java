package com.team3.central.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.SessionTokenRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.OrganizerEntity;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class OrganizerServiceTest {
  private OrganizerService organizerService;
  @Mock
  private OrganizerRepository organizerRepository;
  @Mock
  private SessionTokenRepository sessionTokenRepository;
  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock
  private ConfirmationTokenService confirmationTokenService;
  @Mock
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    organizerService = new OrganizerService(organizerRepository, sessionTokenRepository,
        bCryptPasswordEncoder, confirmationTokenService, emailService);
  }

  @Test
  void testSignUpWithNewOrganizer() {
    //given
    String name = "John";
    String email = "john@example.com";
    String password = "password";
    OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");
    when(organizerRepository.save(any(OrganizerEntity.class))).thenReturn(organizerEntity);

    //when
    ResponseEntity<OrganizerEntity> responseEntity = organizerService.signUp(name, email, password);

    //then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(organizerEntity.getId(), responseEntity.getBody().getId());
    assertEquals(organizerEntity.getEmail(), responseEntity.getBody().getEmail());
    assertEquals(organizerEntity.getName(), responseEntity.getBody().getName());
    assertEquals(organizerEntity.getEvents(), responseEntity.getBody().getEvents());
    assertFalse(organizerEntity.getIsAuthorised());
    verify(organizerRepository, times(1)).findByEmail(email);
    verify(bCryptPasswordEncoder, times(1)).encode(password);
    verify(organizerRepository, times(1)).save(any(OrganizerEntity.class));
    verify(emailService, times(1)).sendSimpleMessage(eq(email), eq("Verify Your account"),
        anyString());
  }


  @Test
  void testSignUpWithExistingUnauthorisedOrganizer() {
    //given
    String name = "John";
    String email = "john@example.com";
    String password = "password";
    OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(false);
    ConfirmationToken confirmationToken = new ConfirmationToken(organizerEntity);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));

    //when
    ResponseEntity<OrganizerEntity> responseEntity = organizerService.signUp(name, email, password);

    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(organizerRepository, times(2)).findByEmail(email);
    verify(confirmationTokenService, times(1)).saveConfirmationToken(argThat(token ->
        token.getOrganizerEntity().getName().equals(organizerEntity.getName()) &&
            token.getOrganizerEntity().getEmail().equals(organizerEntity.getEmail()) &&
            token.getOrganizerEntity().getPassword().equals(organizerEntity.getPassword())
    ));
    verify(emailService, times(1)).sendSimpleMessage(eq(email), eq("Verify Your account"),
        anyString());
  }

  @Test
  void testSignUpOrganizerExistsAndAuthorized() {
    //given
    String name = "Test Organizer";
    String email = "test@example.com";
    String password = "testpassword";

    OrganizerEntity organizer = new OrganizerEntity(name, email, password);
    organizer.setId(1L);
    organizer.setIsAuthorised(true);

    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizer));

    //when
    ResponseEntity<OrganizerEntity> response = organizerService.signUp(name, email, password);

    //then
    verify(emailService, never()).sendSimpleMessage(any(String.class), any(String.class),
        any(String.class));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testConfirmOrganizerNotFound() {
    //given
    String id = "1";
    String token = "testtoken";

    when(confirmationTokenService.getToken(token)).thenReturn(Optional.empty());

    //when
    ResponseEntity<OrganizerEntity> response = organizerService.confirm(id, token);

    //then
    verify(organizerRepository, never()).saveAndFlush(any(OrganizerEntity.class));
    verify(confirmationTokenService, never()).saveConfirmationToken(any(ConfirmationToken.class));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

}

