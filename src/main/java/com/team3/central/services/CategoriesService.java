package com.team3.central.services;

import com.team3.central.mappers.CategoryMapper;
import com.team3.central.repositories.CategoryRepository;
import com.team3.central.repositories.entities.Category;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriesService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = new CategoryMapper();
    }

    public com.team3.central.openapi.model.Category addCategory(String name) {
        Category category = new Category(name);

        categoryRepository.save(category);
        return categoryMapper.convertToModel(category);
    }

    public List<com.team3.central.openapi.model.Category> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::convertToModel)
                .collect(Collectors.toList());
    }

    public boolean existsByName(String name) {
        return categoryRepository.findAll()
                .stream()
                .anyMatch(c -> c.getName().equals(name));
    }
    public Set<Category> getCategoriesFromIds(List<Integer> categoriesIds)
        throws IllegalArgumentException {

        Set<Category> categories = categoryRepository.findAll()
            .stream()
            .filter(category -> categoriesIds.contains(category.getId().intValue()))
            .collect(Collectors.toSet());
        if(categories.size() != categoriesIds.size()) {
            throw new IllegalArgumentException("Invalid categories");
        }
        return categories;
    }
}
