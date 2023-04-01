package com.team3.central.services;

import com.team3.central.repositories.ConfirmationTokenRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationTokenRepository;

  public void saveConfirmationToken(ConfirmationToken token) {
    confirmationTokenRepository.saveAndFlush(token);
  }

  public Optional<ConfirmationToken> getToken(String token) {
    return confirmationTokenRepository.findByToken(token);
  }


  public boolean isTokenExpired(ConfirmationToken token) {
    return LocalDateTime.now().isAfter(token.getExpiresAt());
  }
}
