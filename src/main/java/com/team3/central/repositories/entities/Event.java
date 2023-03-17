package com.team3.central.repositories.entities;

import com.team3.central.repositories.entities.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String title;
    private Long startTime;
    private Long endTime;
    private Long latitude;
    private Long longitude;
    private String name;
    private Long freePlace;
    private String placeSchema;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

//    @ManyToMany(cascade = { CascadeType.ALL })
//    @JoinTable(
//            name = "events-categories",
//            joinColumns = { @JoinColumn(name = "event_id") },
//            inverseJoinColumns = { @JoinColumn(name = "category_id") }


    public Event() {
    }

    public Event(String title,Long startTime,Long endTime,Long latitude,Long longitude, String name,
                 Long freePlace, Organizer organizer, String placeSchema) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.freePlace = freePlace;
        this.organizer = organizer;
        this.placeSchema = placeSchema;
    }

}
