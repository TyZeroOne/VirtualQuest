package org.virtualquest.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Создание категории
    @Transactional
    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateRatingException("Category already exists");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    // Получение всех категорий
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Поиск по ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Поиск по названию
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    // Обновление категории
    @Transactional
    public Category updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (name != null) category.setName(name);
        if (description != null) category.setDescription(description);
        return categoryRepository.save(category);
    }

    // Удаление категории
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}