package com.team3.central.repositories;

import com.team3.central.repositories.entities.SessionToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface SessionTokenRepository extends CrudRepository<SessionToken, Long> {

  Optional<SessionToken> findByToken(String token);
}
