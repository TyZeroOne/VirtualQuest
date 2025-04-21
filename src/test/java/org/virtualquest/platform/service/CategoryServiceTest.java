package org.virtualquest.platform.service;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
        verify(categoryRepository).save(any());
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
        assertEquals("Desc", updated.getDescription()); // Описание не изменилось
    }
}