package org.virtualquest.platform.controller;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCategory_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .param("name", "Puzzle")
                        .param("description", "Mind-bending puzzles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Puzzle"));
    }

    @Test
    void deleteCategory_Success() throws Exception {
        Category category = new Category();
        category.setName("Temp");
        category.setDescription("To delete");
        Category category_ = categoryRepository.save(category);
        mockMvc.perform(delete("/api/categories/" + category_.getId()))
                .andExpect(status().isNoContent());
    }
}