package com.team3.central.services;

import com.team3.central.openapi.model.OrganizerPatch;
import com.team3.central.repositories.OrganizerRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.Event;
import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;
import com.team3.central.services.exceptions.AlreadyExistsException;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.NotFoundException;
import com.team3.central.services.exceptions.UnAuthorizedException;
import com.team3.central.services.exceptions.WrongTokenException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
  public OrganizerEntity signUp(String name, String email,
      String password) throws AlreadyExistsException {
    boolean organizerExists = organizerRepository.findByEmail(email).isPresent();

    if (organizerExists) {
      var organizer = organizerRepository.findByEmail(email).get();
      if (!organizer.isAuthorized()) {
        sendEmailWithConfirmationToken(organizer);
      }
      throw new AlreadyExistsException("Organizer already exists");
    }

    String encodedPassword = bCryptPasswordEncoder.encode(password);
    OrganizerEntity organizerEntity = new OrganizerEntity(name, email, encodedPassword);
    organizerRepository.save(organizerEntity);
    sendEmailWithConfirmationToken(organizerEntity);

    return organizerEntity;
  }

  // If organizer exists and hasn't been authorized and token is valid -> authorize organizer -> code = 202
  // If organizer doesn't exist or has already been confirmed -> do nothing -> code = 400
  public void confirm(String id, String token) throws Exception {
    try {
      long organizerId = Long.parseLong(id);

      var confirmationToken = confirmationTokenService.getToken(token);

      if (confirmationToken.isEmpty() ||
          (confirmationToken.get().getOrganizerEntity().getId() != organizerId) ||
          confirmationTokenService.isTokenExpired(confirmationToken.get())) {
        throw new WrongTokenException("Confirmation token doesn't exist or is expired or organizerId doesn't match");
      }

      var organizer = organizerRepository.findById(organizerId);
      if (organizer.isEmpty()) {
        throw new NotFoundException("Organizer for id was not found");
      } else if (organizer.get().isAuthorized()) {
        throw new AlreadyExistsException("Organizer is already confirmed");
      }

      organizer.get().setStatus(OrganizerStatus.AUTHORIZED);
      confirmationToken.get().setConfirmedAt(LocalDateTime.now());
      organizerRepository.saveAndFlush(organizer.get());
      confirmationTokenService.saveConfirmationToken(confirmationToken.get());
    }
    catch (NumberFormatException numberFormatException) {
      throw new BadIdentificationException("Id in wrong format");
    }
  }

  public String login(String email, String passowrd)
      throws NotFoundException, UnAuthorizedException, BadIdentificationException {
    var organizer = organizerRepository.findByEmail(email);
    if (organizer.isEmpty() ) {
      throw new NotFoundException("no organizer found");
    }
    else if (!organizer.get().isAuthorized())
      throw new UnAuthorizedException("organizer not authorized");
    if (!bCryptPasswordEncoder.matches(passowrd, organizer.get().getPassword())) {
      throw new BadIdentificationException("email or password mismatch");
    }

    return jwtService.generateToken(organizer.get());
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

  public void patchOrganizer(Long id, OrganizerPatch organizerPatch)
      throws IndexOutOfBoundsException {
    if (!organizerRepository.existsById(id)) {
      throw new IndexOutOfBoundsException("Id does not exist");
    }
    OrganizerEntity organizerToUpdate = organizerRepository.findById(id).get();
    if (organizerToUpdate.getStatus() == OrganizerStatus.DELETED) {
      throw new IndexOutOfBoundsException("Organizer already deleted");
    }
    if (organizerPatch.getName() != null) {
      organizerToUpdate.setName(organizerPatch.getName());
    }
    if (organizerPatch.getPassword() != null) {
      organizerToUpdate.setPassword(bCryptPasswordEncoder.encode(organizerPatch.getPassword()));
    }
    organizerRepository.save(organizerToUpdate);
  }
}
