package com.team3.central.services;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.entities.Category;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class CategoriesServiceTest {

  private static CategoryRepository mockCategoryRepository;
  private static CategoriesService categoriesService;

  @BeforeAll
  static void setUp() {
    mockCategoryRepository = Mockito.mock(CategoryRepository.class);
    categoriesService = new CategoriesService(mockCategoryRepository);
  }

  @Test
  void addCategory() {
    // given
    Category category = new Category("TestCategory");
    when(mockCategoryRepository.save(any(Category.class))).thenReturn(category);

    // when
    com.team3.central.openapi.model.Category result = categoriesService.addCategory("TestCategory");

    // then
    assertThat(result)
        .isNotNull()
        .extracting(com.team3.central.openapi.model.Category::getName)
        .isEqualTo(category.getName());
  }

  @Test
  void getAllCategories() {
    // given
    List<Category> categories = new ArrayList<>();
    categories.add(new Category("TestCategory1"));
    categories.add(new Category("TestCategory2"));
    when(mockCategoryRepository.findAll()).thenReturn(categories);

    // when
    List<com.team3.central.openapi.model.Category> result = categoriesService.getAllCategories();

    // then
    assertThat(result)
        .isNotNull()
        .hasSize(categories.size());
  }

  @Test
  void existsByName() {
    // given
    String categoryName = "TestCategory";
    Category category = new Category(categoryName);
    List<Category> categories = new ArrayList<>();
    categories.add(category);
    when(mockCategoryRepository.findAll()).thenReturn(categories);

    // when
    boolean result = categoriesService.existsByName(categoryName);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void notExistsByName() {
    // given
    String categoryName = "TestCategory";
    Category category = new Category(categoryName);
    List<Category> categories = new ArrayList<>();
    categories.add(category);
    when(mockCategoryRepository.findAll()).thenReturn(categories);

    // when
    boolean result = categoriesService.existsByName("SomeOtherCategory");

    // then
    assertThat(result).isFalse();
  }
}
