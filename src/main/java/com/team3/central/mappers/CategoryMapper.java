package com.team3.central.mappers;

import com.team3.central.openapi.model.Category;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryMapper {
  public com.team3.central.repositories.entities.Category convertToEntity(Category category) {
    com.team3.central.repositories.entities.Category categoryDTO = new com.team3.central.repositories.entities.Category();
    categoryDTO.setId(category.getId());
    categoryDTO.setName(category.getName());
    return categoryDTO;
  }
  public Category convertToModel(com.team3.central.repositories.entities.Category category) {
    Category categoryApi = new Category();
    categoryApi.setId(category.getId());
    categoryApi.setName(category.getName());
    return categoryApi;
  }
}
