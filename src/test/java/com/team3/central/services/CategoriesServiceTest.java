package com.team3.central.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.entities.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;


public class CategoriesServiceTest {
  private static CategoryRepository categoryRepository;
  private static CategoriesService categoriesService;
  private static List<Category> categories;
  @BeforeAll
  static void setUp() {
    categoryRepository = Mockito.mock(CategoryRepository.class);
    categoriesService = new CategoriesService(categoryRepository);
    categories = new ArrayList<>();
    categories.add(new Category("Category1"));
    categories.add(new Category("Category2"));
  }

  @Test
  void addCategory() {
    // given
    Category category = new Category("TestCategory");
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

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
    when(categoryRepository.findAll()).thenReturn(categories);

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
    when(categoryRepository.findAll()).thenReturn(categories);

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
    when(categoryRepository.findAll()).thenReturn(categories);

    // when
    boolean result = categoriesService.existsByName("SomeOtherCategory");

    // then
    assertThat(result).isFalse();
  }



  @Test
  public void shouldReturnCategoriesWithValidIds() {
    List<Integer> categoriesIds = List.of(1, 2);
    Set<Category> expectedCategories = Set.copyOf(categories);
    when(categoryRepository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(expectedCategories);

    Set<Category> actualCategories = categoriesService.getCategoriesFromIds(categoriesIds);

    assertEquals(expectedCategories, actualCategories);
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionWithInvalidIds() {
    List<Integer> categoriesIds = List.of(1, 2, 3);
    Set<Category> existingCategories = Set.copyOf(categories);
    when(categoryRepository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(existingCategories);
    assertThrows(IllegalArgumentException.class, () -> categoriesService.getCategoriesFromIds(categoriesIds));
  }

}
