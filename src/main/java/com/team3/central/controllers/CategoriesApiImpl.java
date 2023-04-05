package com.team3.central.controllers;

import com.team3.central.openapi.api.CategoriesApi;
import com.team3.central.openapi.model.Category;
import com.team3.central.services.CategoriesService;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CategoriesApiImpl implements CategoriesApi {

  CategoriesService categoriesService;

  /**
   * POST /categories : Create new category
   *
   * @param categoryName name of category (required)
   * @return created (status code 201) or category already exist (status code 400)
   */
  @Override
  public ResponseEntity<Category> addCategories(
      @NotNull @ApiParam(value = "name of category", required = true) @Valid @RequestParam(value = "categoryName", required = true) String categoryName) {
    if (categoriesService.existsByName(categoryName)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    Category category = categoriesService.addCategory(categoryName);
    return new ResponseEntity<>(category, HttpStatus.OK);
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
