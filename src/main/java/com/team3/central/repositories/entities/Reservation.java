package com.team3.central.repositories.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long placeOnSchema;
  private String reservationToken;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  public Reservation(Event event, Long placeOnSchema, String reservationToken) {
    this.event = event;
    this.placeOnSchema = placeOnSchema;
    this.reservationToken = reservationToken;
  }
}
