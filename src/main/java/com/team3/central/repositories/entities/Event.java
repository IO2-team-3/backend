package com.team3.central.repositories.entities;

import com.team3.central.repositories.entities.enums.EventStatus;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String title;
    private Long startTime;
    private Long endTime;
    private String latitude;
    private String longitude;
    private String name;
    private Long freePlace;

    @Column(columnDefinition="TEXT")
    private String placeSchema;

    private Long maxPlace;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private OrganizerEntity organizer;

    @OneToMany(mappedBy = "event")
    private Set<Reservation> reservations;

    @ManyToMany
    @JoinTable(
        name = "events_categories",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

}
