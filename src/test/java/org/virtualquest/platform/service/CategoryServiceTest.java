package org.virtualquest.platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByName("Adventure")).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category category = categoryService.createCategory("Adventure", "Exciting quests");

        assertEquals("Adventure", category.getName());
        assertEquals("Exciting quests", category.getDescription());
        verify(categoryRepository).save(any());
    }

    @Test
    void createCategory_Duplicate_ThrowsException() {
        when(categoryRepository.existsByName("Adventure")).thenReturn(true);

        assertThrows(DuplicateRatingException.class,
                () -> categoryService.createCategory("Adventure", "Duplicate"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAllCategories_ReturnsList() {
        Category cat1 = new Category();
        cat1.setName("Cat1");
        Category cat2 = new Category();
        cat2.setName("Cat2");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        assertEquals("Cat1", categories.get(0).getName());
        assertEquals("Cat2", categories.get(1).getName());
    }

    @Test
    void getCategoryById_Found() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Cat");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Cat", result.get().getName());
    }

    @Test
    void getCategoryById_NotFound() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByName_Found() {
        Category cat = new Category();
        cat.setName("Adventure");

        when(categoryRepository.findByName("Adventure")).thenReturn(Optional.of(cat));

        Optional<Category> result = categoryService.findByName("Adventure");

        assertTrue(result.isPresent());
        assertEquals("Adventure", result.get().getName());
    }

    @Test
    void findByName_NotFound() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findByName("Unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void updateCategory_ValidData() {
        Category existing = new Category();
        existing.setName("Old");
        existing.setDescription("Desc");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category updated = categoryService.updateCategory(1L, "New", null);

        assertEquals("New", updated.getName());
        assertEquals("Desc", updated.getDescription()); // описание осталось
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(1L, "New", "Desc"));
    }

    @Test
    void deleteCategory_CallsRepositoryDelete() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
