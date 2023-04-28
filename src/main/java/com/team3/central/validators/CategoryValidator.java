package com.team3.central.validators;

import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

  public void validateCategoryName(String categoryName) throws IllegalArgumentException {
    if (categoryName == null || categoryName.length() < 1) {
      throw new IllegalArgumentException("Category name cannot be null or empty");
    }
  }

  public void validateCategoryId(Long categoryId) throws IllegalArgumentException {
    if (categoryId == null || categoryId < 1) {
      throw new IllegalArgumentException("Category id cannot be null or less than 1");
    }
  }
}
