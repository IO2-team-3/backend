package com.team3.central.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team3.central.openapi.model.OrganizerPatch;
import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;
import com.team3.central.services.exceptions.AlreadyExistsException;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.NotFoundException;
import com.team3.central.services.exceptions.WrongTokenException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    // then
    OrganizerEntity organizer = assertDoesNotThrow(()->organizerService.signUp(name,email,password));
    assertThat(organizer).usingRecursiveComparison().isEqualTo(organizerEntity);
  }


  @Test
  void testSignUpWithExistingUnauthorisedOrganizer() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setStatus(OrganizerStatus.UNAUTHORIZED);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));

    // then
    assertThrows(AlreadyExistsException.class, ()->organizerService.signUp(name, email, password));
  }

  @Test
  void testSignUpOrganizerExistsAndAuthorized() {
    // given
    final String name = "Test Organizer";
    final String email = "test@example.com";
    final String password = "testpassword";

    final OrganizerEntity organizer = new OrganizerEntity(name, email, password);
    organizer.setId(1L);
    organizer.setStatus(OrganizerStatus.AUTHORIZED);

    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizer));

    // then
    assertThrows(AlreadyExistsException.class, ()->organizerService.signUp(name, email, password));

  }

  @Test
  void testConfirmOrganizerNotFound() {
    // given
    final String id = "1";
    final String token = "testtoken";

    when(confirmationTokenService.getToken(token)).thenReturn(Optional.empty());

    // when
    assertThrows(WrongTokenException.class, () -> organizerService.confirm(id, token));
  }

  @Test
  void testLoginOrganizerExists() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final String token = "token123";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setStatus(OrganizerStatus.AUTHORIZED);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));
    when(jwtService.generateToken(organizerEntity)).thenReturn(token);
    when(bCryptPasswordEncoder.matches(password, organizerEntity.getPassword())).thenReturn(true);

    // then
    assertDoesNotThrow(() -> {
      organizerService.login(email, password);
    });

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
   assertThrows(NotFoundException.class, ()->{
     organizerService.login(email,password);
   });
  }

  @Test
  void testLoginWrongPassword() {
    // given
    final String name = "John";
    final String email = "john@example.com";
    final String password = "password";
    final String token = "token123";
    final OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setStatus(OrganizerStatus.AUTHORIZED);
    when(organizerRepository.findByEmail(email)).thenReturn(Optional.of(organizerEntity));
    when(jwtService.generateToken(organizerEntity)).thenReturn(token);
    when(bCryptPasswordEncoder.matches(password, organizerEntity.getPassword())).thenReturn(false);

    // then
    assertThrows(BadIdentificationException.class, ()->{
      organizerService.login(email,password);
    });
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

  @Test
  public void throwWhenDeletingAlreadyDeletedOrganizer() {
    // given
    Long id = 100L;
    OrganizerEntity deletedOrganizer = new OrganizerEntity();
    deletedOrganizer.setStatus(OrganizerStatus.DELETED);
    deletedOrganizer.setId(id);

    when(organizerRepository.existsById(id)).thenReturn(true);
    when(organizerRepository.findById(id)).thenReturn(Optional.of(deletedOrganizer));

    assertThatThrownBy(() -> {
      // when
      organizerService.deleteOrganizer(id);
    })
    // then
        .isInstanceOf(IndexOutOfBoundsException.class)
        .hasMessage("Organizer already deleted");
  }

  @Test
  void testPatchOrganizerWithName() {
    // given
    Long id = 1L;
    String newName = "newName";
    OrganizerPatch patch = new OrganizerPatch();
    patch.setName(newName);
    OrganizerEntity organizerEntity = new OrganizerEntity();
    organizerEntity.setId(id);
    organizerEntity.setName("oldName");
    organizerEntity.setStatus(OrganizerStatus.AUTHORIZED);

    when(organizerRepository.existsById(id)).thenReturn(true);
    when(organizerRepository.findById(id)).thenReturn(Optional.of(organizerEntity));

    // when
    organizerService.patchOrganizer(id, patch);

    // then
    assertEquals(newName, organizerEntity.getName());
  }

  @Test
  void testPatchOrganizerWithPassword() {
    // given
    Long id = 1L;
    String newPassword = "newPassword";
    OrganizerPatch patch = new OrganizerPatch();
    patch.setPassword(newPassword);
    OrganizerEntity organizerEntity = new OrganizerEntity();
    organizerEntity.setId(id);
    organizerEntity.setPassword("oldPassword");
    organizerEntity.setStatus(OrganizerStatus.AUTHORIZED);

    when(organizerRepository.existsById(id)).thenReturn(true);
    when(organizerRepository.findById(id)).thenReturn(Optional.of(organizerEntity));
    when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedPassword");

    // when
    organizerService.patchOrganizer(id, patch);

    // then
    assertEquals("encodedPassword", organizerEntity.getPassword());
  }

  @Test
  void testPatchOrganizerWithNonExistingId() {
    // given
    Long id = 1L;
    OrganizerPatch patch = new OrganizerPatch();

    when(organizerRepository.existsById(id)).thenReturn(false);

    // then & then
    assertThrows(IndexOutOfBoundsException.class, () -> organizerService.patchOrganizer(id, patch));
  }

  @Test
  void testPatchOrganizerWithDeletedOrganizer() {
    // given
    Long id = 1L;
    OrganizerPatch patch = new OrganizerPatch();
    OrganizerEntity organizerEntity = new OrganizerEntity();
    organizerEntity.setId(id);
    organizerEntity.setStatus(OrganizerStatus.DELETED);

    when(organizerRepository.existsById(id)).thenReturn(true);
    when(organizerRepository.findById(id)).thenReturn(Optional.of(organizerEntity));

    // then & then
    assertThrows(IndexOutOfBoundsException.class, () -> organizerService.patchOrganizer(id, patch));
  }

}

