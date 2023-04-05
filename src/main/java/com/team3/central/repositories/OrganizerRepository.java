package com.team3.central.repositories;

import com.team3.central.repositories.entities.OrganizerEntity;
import com.team3.central.repositories.entities.enums.OrganizerStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OrganizerRepository extends JpaRepository<OrganizerEntity, Long> {

  Optional<OrganizerEntity> findById(long id);

  Optional<OrganizerEntity> findByEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE OrganizerEntity o " +
      "SET o.status = ?2 " +
      "WHERE o.id = ?1")
  int updateIsAuthorised(long id, OrganizerStatus status);

}
