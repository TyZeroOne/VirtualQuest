package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    public void testSaveAndFindCategory() {
        Category category = new Category();
        category.setName("Adventure");
        category.setDescription("Exciting quests");
        categoryRepository.save(category);

        Category found = categoryRepository.findByName("Adventure").orElseThrow();
        assertEquals("Exciting quests", found.getDescription());
    }

    @Test
    public void testCategoryUniqueness() {
        Category category1 = new Category();
        category1.setName("Puzzle");
        category1.setDescription("Puzzle quests");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Puzzle");
        category2.setDescription("Another puzzle");

        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryRepository.saveAndFlush(category2);
        });
    }
}