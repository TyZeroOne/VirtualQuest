package org.virtualquest.platform.controller;

import org.virtualquest.platform.dto.CategoryDTO;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Создание категории
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(
            @RequestBody CategoryDTO category
    ) {
        return ResponseEntity.ok(categoryService.createCategory(
                category.getName(),
                category.getDescription())
        );
    }

    // Получение всех категорий
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Получение категории по ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Обновление категории
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, name, description));
    }

    // Удаление категории
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}