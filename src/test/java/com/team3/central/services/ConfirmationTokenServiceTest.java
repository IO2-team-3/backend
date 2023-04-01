package com.team3.central.services;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team3.central.repositories.ConfirmationTokenRepository;
import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.OrganizerEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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

  @ParameterizedTest
//  @ValueSource(ints = {1, 3, 5, -3, 15, Integer.MAX_VALUE}) // six number
  @MethodSource("testData")
  public void testIsTokenExpired(int expirationOffset, boolean hasExpired) {
    // given
    ConfirmationToken token = new ConfirmationToken(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(expirationOffset),
        new OrganizerEntity("John Doe", "johndoe@example.com", "password")
    );
    // then
    assertThat(confirmationTokenService.isTokenExpired(token)).isEqualTo(hasExpired);
  }

  private static Stream<Arguments> testData() {
    return Stream.of(
        Arguments.of(10,  false),
        Arguments.of(-1, true)
    );
  }

}