package com.team3.central.repositories;

import com.team3.central.repositories.entities.Organizer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

  Optional<Organizer> findById(long id);

  Optional<Organizer> findByEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE Organizer o " +
      "SET o.isAuthorised = ?2 " +
      "WHERE o.id = ?1")
  int updateIsAuthorised(long id, boolean isAuthorised);
}
