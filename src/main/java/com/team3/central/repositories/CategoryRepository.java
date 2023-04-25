package com.team3.central.repositories;

import com.team3.central.repositories.entities.Category;
import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

  Category findById(long id);

  List<Category> findAll();
  Set<Category> findAllByIdIn(Set<Long> ids);
}

