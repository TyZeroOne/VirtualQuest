package org.virtualquest.platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.virtualquest.platform.dto.CategoryDTO;
import org.virtualquest.platform.exception.GlobalExceptionHandler;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.filter.JwtAuthFilter;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.virtualquest.platform.util.JwtUtils;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CategoryControllerIT {

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // именно @MockBean, чтобы мокировать бин в контексте Spring
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCategory_Success() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Test category");
        dto.setDescription("Description");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(dto.getName());
        savedCategory.setDescription(dto.getDescription());

        when(categoryService.createCategory(dto.getName(), dto.getDescription())).thenReturn(savedCategory);

        mockMvc.perform(post("/api/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategory.getId()))
                .andExpect(jsonPath("$.name").value(savedCategory.getName()))
                .andExpect(jsonPath("$.description").value(savedCategory.getDescription()));

        verify(categoryService).createCategory(dto.getName(), dto.getDescription());
    }

    @Test
    void testGetAllCategories_Success() throws Exception {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Cat 1");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Cat 2");

        List<Category> categories = Arrays.asList(cat1, cat2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(categories.size()))
                .andExpect(jsonPath("$[0].id").value(cat1.getId()))
                .andExpect(jsonPath("$[1].id").value(cat2.getId()));

        verify(categoryService).getAllCategories();
    }

    @Test
    void testGetCategory_Found() throws Exception {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Found category");

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(cat));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cat.getId()))
                .andExpect(jsonPath("$.name").value(cat.getName()));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    void testGetCategory_NotFound() throws Exception {
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById(999L);
    }

    @Test
    void testUpdateCategory_Success() throws Exception {
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated name");
        updatedCategory.setDescription("Updated description");

        when(categoryService.updateCategory(eq(1L), eq("Updated name"), eq("Updated description")))
                .thenReturn(updatedCategory);

        mockMvc.perform(put("/api/categories/1")
                        .param("name", "Updated name")
                        .param("description", "Updated description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCategory.getId()))
                .andExpect(jsonPath("$.name").value(updatedCategory.getName()))
                .andExpect(jsonPath("$.description").value(updatedCategory.getDescription()));

        verify(categoryService).updateCategory(1L, "Updated name", "Updated description");
    }

    @Test
    void testDeleteCategory_Success() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void testUpdateCategory_NotFound() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(), any()))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(put("/api/categories/1")
                        .param("name", "Name")
                        .param("description", "Desc"))
                .andExpect(status().isNotFound());

        verify(categoryService).updateCategory(1L, "Name", "Desc");
    }

}
