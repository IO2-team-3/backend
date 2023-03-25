package com.team3.central.repositories;

import com.team3.central.repositories.entities.ConfirmationToken;
import com.team3.central.repositories.entities.SessionToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface SessionTokenRepository extends CrudRepository<SessionToken, Long> {
  Optional<SessionToken> findByToken(String token);
}
