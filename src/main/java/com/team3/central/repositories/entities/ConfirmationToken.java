package com.team3.central.repositories.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "confirmation_token")
public class ConfirmationToken {

  @SequenceGenerator(
      name = "confirmation_token_sequence",
      sequenceName = "confirmation_token_sequence",
      allocationSize = 1
  )
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "confirmation_token_sequence"
  )
  private Long id;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  private LocalDateTime confirmedAt;

  @ManyToOne
  @JoinColumn(
      nullable = false,
      name = "organizer_id"
  )
  private OrganizerEntity organizerEntity;

  public ConfirmationToken(String token,
      LocalDateTime createdAt,
      LocalDateTime expiresAt,
      OrganizerEntity organizerEntity) {
    this.token = token;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.organizerEntity = organizerEntity;
  }

  public ConfirmationToken(OrganizerEntity organizerEntity) {
    this.token = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.expiresAt = LocalDateTime.now().plusMinutes(30);
    this.organizerEntity = organizerEntity;
  }
}