package com.team3.central.controllers;

import com.team3.central.openapi.api.CategoriesApi;
import com.team3.central.openapi.model.Category;
import com.team3.central.services.CategoriesService;
import com.team3.central.services.exceptions.CategoryExistsException;
import com.team3.central.validators.CategoryValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoriesApiImpl implements CategoriesApi {

  final private CategoriesService categoriesService;
  final private CategoryValidator categoryValidator;

  /**
   * POST /categories : Create new category
   *
   * @param categoryName name of category (required)
   * @return created (status code 201) or category already exist (status code 400) or invalid
   * session (status code 403)
   */
  @Override
  public ResponseEntity<Category> addCategories(String categoryName) {
    try {
      categoryValidator.validateCategoryName(categoryName);

      categoryName = categoryName.toLowerCase();
      Category category = categoriesService.addCategory(categoryName);

      return new ResponseEntity<>(category, HttpStatus.CREATED);
    } catch (CategoryExistsException | IllegalArgumentException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * GET /categories : Return list of all categories
   *
   * @return successful operation (status code 200)
   */
  @Override
  public ResponseEntity<List<Category>> getCategories() {
    return new ResponseEntity<>(categoriesService.getAllCategories(), HttpStatus.OK);
  }
}
