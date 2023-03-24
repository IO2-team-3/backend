package com.team3.central.services;

import com.team3.central.repositories.ConfirmationTokenRepository;
import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.Organizer;
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

  public ResponseEntity<Organizer> signUp(String name, String email,
      String password) {
    boolean userExists = organizerRepository
        .findByEmail(email)
        .isPresent();

    if (userExists) {
      // TODO if email not confirmed send confirmation email.

      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//      throw new IllegalStateException("email already taken");
    }

    Organizer organizer = new Organizer(name, email, password);
    organizer.setIsAuthorised(false);
    String encodedPassword = bCryptPasswordEncoder
        .encode(organizer.getPassword());

    organizer.setPassword(encodedPassword);

    var rse = organizerRepository.save(organizer);

    String token = UUID.randomUUID().toString();

    ConfirmationToken confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        organizer
    );

    confirmationTokenService.saveConfirmationToken(confirmationToken);

//    com.team3.central.openapi.model.Organizer organizerModelApi = new com.team3.central.openapi.model.Organizer();
//    organizerModelApi.setEmail(organizer.getEmail());
//    organizerModelApi.setId(organizer.getId());
//    organizerModelApi.setName(organizer.getEmail());
//    organizerModelApi.setPassword(organizer.getEmail());
    emailService.sendSimpleMessage(organizer.getEmail(), "Verify Your account", token);
    return new ResponseEntity<>(organizer, HttpStatus.CREATED);

  }

  public ResponseEntity<com.team3.central.openapi.model.Organizer> confirm(String id,
      String token) {
    long organizerId = Long.parseLong(id);
    var confirmationToken = confirmationTokenRepository
        .findByToken(token);
    if (confirmationToken.isPresent()
        && confirmationToken.get().getOrganizer().getId() == organizerId) {
      var organizer = organizerRepository.findById(organizerId);
      if (organizer.isPresent() && LocalDateTime.now()
          .isBefore(confirmationToken.get().getExpiresAt())) {

        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
        organizerRepository.updateIsAuthorised(organizerId, true);
      }

    }
    com.team3.central.openapi.model.Organizer organizerModelApi = new com.team3.central.openapi.model.Organizer();
    organizerModelApi.setEmail(confirmationToken.get().getOrganizer().getEmail());
    organizerModelApi.setId(confirmationToken.get().getOrganizer().getId());
    organizerModelApi.setName(confirmationToken.get().getOrganizer().getEmail());
    organizerModelApi.setPassword(confirmationToken.get().getOrganizer().getEmail());
    return new ResponseEntity<com.team3.central.openapi.model.Organizer>(organizerModelApi,
        HttpStatus.ACCEPTED);
  }
}
