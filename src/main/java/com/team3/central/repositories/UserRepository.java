package com.team3.central.repositories;

import com.team3.central.repositories.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends CrudRepository<User, Long> {

  User findById(long id);
}
