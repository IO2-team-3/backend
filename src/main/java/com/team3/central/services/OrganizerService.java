package com.team3.central.services;

import com.team3.central.repositories.ConfirmationTokenRepository;
import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.OrganizerEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrganizerService {

  private final OrganizerRepository organizerRepository;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenService confirmationTokenService;
  private final EmailService emailService;

  private ConfirmationToken generateConfirmationToken(OrganizerEntity organizerEntity) {
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        organizerEntity
    );

    confirmationTokenService.saveConfirmationToken(confirmationToken);
    return confirmationToken;
  }

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
    boolean organizerExists = organizerRepository
        .findByEmail(email)
        .isPresent();

    if (organizerExists) {
      var organizer = organizerRepository.findByEmail(email).get();

      if (!organizer.getIsAuthorised()) {
        sendEmailWithConfirmationToken(organizer);
      }

      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(false);
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
    var confirmationToken = confirmationTokenRepository.findByToken(token);
    if (confirmationToken.isPresent() &&
        confirmationToken.get().getOrganizerEntity().getId() == organizerId) {
      var organizer = organizerRepository.findById(organizerId);
      if (organizer.isPresent()
          && LocalDateTime.now().isBefore(confirmationToken.get().getExpiresAt()) &&
          !organizer.get().getIsAuthorised()) {

        organizer.get().setIsAuthorised(true);
        confirmationToken.get().setConfirmedAt(LocalDateTime.now());
        organizerRepository.saveAndFlush(organizer.get());
        confirmationTokenRepository.saveAndFlush(confirmationToken.get());
        return new ResponseEntity<OrganizerEntity>(organizer.get(), HttpStatus.ACCEPTED);
      }
    }

    return new ResponseEntity<OrganizerEntity>(HttpStatus.BAD_REQUEST);
  }
}
