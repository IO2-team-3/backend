package com.team3.central.validators;

public class CategoriesValidator {

  public  boolean validateCategoryName(String categoryName) {
    return categoryName != null && categoryName.length() > 0;
  }
}
