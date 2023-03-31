package com.team3.central.services;

import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.entities.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Set<Category> getCategoriesFromIds(List<Integer> categoriesIds) {
    return categoryRepository.findAll()
        .stream()
        .filter(category -> categoriesIds.contains(category.getId().intValue()))
        .collect(Collectors.toSet());
  }
}
