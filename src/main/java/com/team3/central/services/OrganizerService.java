package com.team3.central.services;

import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrganizerService {

  private final OrganizerRepository organizerRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenService confirmationTokenService;
  private final EmailService emailService;

  private final JwtService jwtService;

  // Generates new confirmation token for given OrganizerEntity and saves it to database
  public @NotNull ConfirmationToken generateConfirmationToken(OrganizerEntity organizerEntity) {
    ConfirmationToken confirmationToken = new ConfirmationToken(organizerEntity);
    confirmationTokenService.saveConfirmationToken(confirmationToken);
    return confirmationToken;
  }

  // Generates token and sends it to given OrganizerEntity
  private void sendEmailWithConfirmationToken(OrganizerEntity organizerEntity) {
    ConfirmationToken confirmationToken = generateConfirmationToken(organizerEntity);
    emailService.sendSimpleMessage(organizerEntity.getEmail(), "Verify Your account",
        confirmationToken.getToken());
  }

  // If organizer doesn't exist -> create organizer, send email with token -> code = 201
  // If organizer exists and hasn't been authorized -> send email with new token -> code = 400
  // If organizer exists and has been authorized -> do nothing -> code = 400
  public ResponseEntity<OrganizerEntity> signUp(String name, String email,
      String password) {
    boolean organizerExists = organizerRepository.findByEmail(email).isPresent();

    if (organizerExists) {
      var organizer = organizerRepository.findByEmail(email).get();
      if (!organizer.getIsAuthorised()) { // can confirmation token expire? Nothing about it in docs
        sendEmailWithConfirmationToken(organizer);
      }
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(false);
    organizerEntity.setStatus(OrganizerStatus.UNAUTHORIZED);
    String encodedPassword = bCryptPasswordEncoder.encode(organizerEntity.getPassword());
    organizerEntity.setPassword(encodedPassword);
    organizerRepository.save(organizerEntity);
    sendEmailWithConfirmationToken(organizerEntity);

    return new ResponseEntity<>(organizerEntity, HttpStatus.CREATED);
  }

  // If organizer exists and hasn't been authorized and token is valid -> authorize organizer -> code = 202
  // If organizer doesn't exist or has already been confirmed -> do nothing -> code = 400
  public ResponseEntity<OrganizerEntity> confirm(String id, String token) {
    long organizerId = Long.parseLong(id);
    var confirmationToken = confirmationTokenService.getToken(token);

    if (confirmationToken.isEmpty() ||
        (confirmationToken.get().getOrganizerEntity().getId() != organizerId) ||
        confirmationTokenService.isTokenExpired(confirmationToken.get())) {
      return new ResponseEntity<OrganizerEntity>(HttpStatus.BAD_REQUEST);
    }

    var organizer = organizerRepository.findById(organizerId);
    if (organizer.isEmpty() || organizer.get().getIsAuthorised()) {
      return new ResponseEntity<OrganizerEntity>(HttpStatus.BAD_REQUEST);
    }

    organizer.get().setIsAuthorised(true);
    organizer.get().setStatus(OrganizerStatus.AUTHORIZED);
    confirmationToken.get().setConfirmedAt(LocalDateTime.now());
    organizerRepository.saveAndFlush(organizer.get());
    confirmationTokenService.saveConfirmationToken(confirmationToken.get());
    return new ResponseEntity<OrganizerEntity>(organizer.get(), HttpStatus.ACCEPTED);
  }

  // If email and password matches to existing Organizer account, then return sessionToken valid for 3 days, code -> 200
  // Otherwise return code -> 400
  public ResponseEntity<String> login(String email, String passowrd) {
    var organizer = organizerRepository.findByEmail(email);
    if (organizer.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (!bCryptPasswordEncoder.matches(passowrd, organizer.get().getPassword())) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    final String jwt = jwtService.generateToken(organizer.get());
    return new ResponseEntity<String>(jwt, HttpStatus.OK);
  }


  public Optional<OrganizerEntity> getOrganizerFromEmail(String username) {
    return organizerRepository.findByEmail(username);
  }

  public void deleteOrganizer(Long id) {
    if(!organizerRepository.existsById(id)) {
      throw new IndexOutOfBoundsException("Id does not exist");
    }
    OrganizerEntity organizerToUpdate = organizerRepository.findById(id).get();
    if(organizerToUpdate.getStatus() == OrganizerStatus.DELETED) {
      throw new IndexOutOfBoundsException("Organizer already deleted");
    }
    final String hashedEmail = bCryptPasswordEncoder.encode(organizerToUpdate.getEmail());

    organizerToUpdate.setStatus(OrganizerStatus.DELETED);
    organizerToUpdate.setEmail(hashedEmail);
    organizerRepository.save(organizerToUpdate);
  }

  public Set<Event> getEventsOfOrganizer(Long id) {
    if(!organizerRepository.existsById(id)) {
      throw new IndexOutOfBoundsException("Id does not exist");
    }
    OrganizerEntity organizer = organizerRepository.findById(id).get();
    return organizer.getEvents();
  }
}
