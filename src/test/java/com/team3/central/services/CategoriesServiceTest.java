package com.team3.central.services;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.entities.Category;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;


public class CategoriesServiceTest {
  private CategoryRepository mockCategoryRepository;
  private CategoriesService categoriesService;

  @BeforeEach
  void setUp() {
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
    assertNotNull(result);
    assertEquals(result.getName(), category.getName());
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
    assertNotNull(result);
    assertEquals(result.size(), categories.size());
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
    assertTrue(result);
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
    assertFalse(result);
  }
}
