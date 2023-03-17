package com.team3.central.repositories.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

//    @ManyToMany(mappedBy = "category")
//    private Set<Event> events = new HashSet<>();

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }
}
