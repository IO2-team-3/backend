package com.team3.central.repositories.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long placeOnSchema;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Reservation() {
    }

    public Reservation(User user,Event event) {
        this.user = user;
        this.event = event;
    }

    public Reservation(User user,Event event,Long placeOnSchema) {
        this.user = user;
        this.event = event;
        this.placeOnSchema = placeOnSchema;
    }
}
