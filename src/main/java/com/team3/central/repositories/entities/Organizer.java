package com.team3.central.repositories.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Table(name = "organizer")
public class Organizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organizer_id")
    private Long id;

    private String name;
    private Boolean isAuthorised;
    private String email;
    private String password;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

    public Organizer() {
    }

    public Organizer(String name,  String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
