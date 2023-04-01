package com.team3.central.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team3.central.repositories.ConfirmationTokenRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.OrganizerEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConfirmationTokenServiceTest {

  private ConfirmationTokenService confirmationTokenService;

  @Mock
  private ConfirmationTokenRepository confirmationTokenRepository;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepository);
  }

  @Test
  public void testIsTokenExpired() {
    // given
    ConfirmationToken token = new ConfirmationToken(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(30),
        new OrganizerEntity("John Doe", "johndoe@example.com", "password")
    );
    // then
    assertFalse(confirmationTokenService.isTokenExpired(token));
    // when
    token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
    // then
    assertTrue(confirmationTokenService.isTokenExpired(token));
  }

}