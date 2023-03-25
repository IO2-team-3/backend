package com.team3.central.repositories;

import com.team3.central.repositories.entities.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findById(long id);
    List<Category> findAll();
}

