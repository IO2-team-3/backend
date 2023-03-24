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

  public ResponseEntity<OrganizerEntity> signUp(String name, String email,
      String password) {
    boolean userExists = organizerRepository
        .findByEmail(email)
        .isPresent();

    if (userExists) {
      // TODO if email not confirmed send confirmation email.

      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//      throw new IllegalStateException("email already taken");
    }

    OrganizerEntity organizerEntity = new OrganizerEntity(name, email, password);
    organizerEntity.setIsAuthorised(false);
    String encodedPassword = bCryptPasswordEncoder
        .encode(organizerEntity.getPassword());

    organizerEntity.setPassword(encodedPassword);

    var rse = organizerRepository.save(organizerEntity);

    String token = UUID.randomUUID().toString();

    ConfirmationToken confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        organizerEntity
    );

    confirmationTokenService.saveConfirmationToken(confirmationToken);

//    com.team3.central.openapi.model.Organizer organizerModelApi = new com.team3.central.openapi.model.Organizer();
//    organizerModelApi.setEmail(organizer.getEmail());
//    organizerModelApi.setId(organizer.getId());
//    organizerModelApi.setName(organizer.getEmail());
//    organizerModelApi.setPassword(organizer.getEmail());
    emailService.sendSimpleMessage(organizerEntity.getEmail(), "Verify Your account", token);
    return new ResponseEntity<>(organizerEntity, HttpStatus.CREATED);

  }

  public ResponseEntity<OrganizerEntity> confirm(String id, String token) {
    long organizerId = Long.parseLong(id);
    var confirmationToken = confirmationTokenRepository.findByToken(token);
    if (confirmationToken.isPresent()
        && confirmationToken.get().getOrganizerEntity().getId() == organizerId) {

      var organizer = organizerRepository.findById(organizerId);
      if (organizer.isPresent() && LocalDateTime.now().isBefore(confirmationToken.get().getExpiresAt())) {

        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
        organizerRepository.updateIsAuthorised(organizerId, true);
      }

    }
    var all =organizerRepository.findAll();
    var organizer = organizerRepository.findById(organizerId);
    var tes= confirmationTokenRepository.findAll();
    return new ResponseEntity<OrganizerEntity>(organizer.get(), HttpStatus.ACCEPTED);
  }
}
