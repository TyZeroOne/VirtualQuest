package org.virtualquest.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.virtualquest.platform.dto.StepDTO;
import org.virtualquest.platform.exception.GlobalExceptionHandler;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.filter.JwtAuthFilter;
import org.virtualquest.platform.model.Step;
import org.virtualquest.platform.service.StepService;
import org.virtualquest.platform.util.JwtUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StepController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class StepControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StepService stepService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void createStep_Success() throws Exception {
        Long questId = 1L;
        StepDTO dto = new StepDTO();
        dto.setDescription("Step 1");
        Step step = new Step();
        step.setId(10L);
        step.setDescription("Step 1");

        when(stepService.createStep(eq(questId), any(StepDTO.class))).thenReturn(step);

        mockMvc.perform(post("/api/steps/quest/{questId}", questId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.description").value("Step 1"));
    }

    @Test
    void updateStep_Success() throws Exception {
        Long stepId = 5L;
        StepDTO dto = new StepDTO();
        dto.setDescription("Updated Step");
        Step step = new Step();
        step.setId(stepId);
        step.setDescription("Updated Step");

        when(stepService.updateStep(eq(stepId), any(StepDTO.class))).thenReturn(step);

        mockMvc.perform(put("/api/steps/{stepId}", stepId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stepId))
                .andExpect(jsonPath("$.description").value("Updated Step"));
    }

    // --- Тест удаления шага ---
    @Test
    void deleteStep_Success() throws Exception {
        Long stepId = 3L;

        doNothing().when(stepService).deleteStep(stepId);

        mockMvc.perform(delete("/api/steps/{stepId}", stepId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getStepsByQuest_Success() throws Exception {
        Long questId = 7L;

        Step step1 = new Step();
        step1.setId(1L);
        step1.setDescription("Step One");

        Step step2 = new Step();
        step2.setId(2L);
        step2.setDescription("Step Two");

        when(stepService.getStepsByQuest(questId)).thenReturn(List.of(step1, step2));

        mockMvc.perform(get("/api/steps/quest/{questId}", questId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].description").value("Step Two"));
    }

    @Test
    void createStep_ResourceNotFoundException() throws Exception {
        Long questId = 999L;
        StepDTO dto = new StepDTO();
        dto.setDescription("Step");

        when(stepService.createStep(eq(questId), any(StepDTO.class)))
                .thenThrow(new ResourceNotFoundException("Quest not found"));

        mockMvc.perform(post("/api/steps/quest/{questId}", questId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Quest not found"));
    }

    @Test
    void updateStep_ResourceNotFoundException() throws Exception {
        Long stepId = 999L;
        StepDTO dto = new StepDTO();
        dto.setDescription("Step");

        when(stepService.updateStep(eq(stepId), any(StepDTO.class)))
                .thenThrow(new ResourceNotFoundException("Step not found"));

        mockMvc.perform(put("/api/steps/{stepId}", stepId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Step not found"));
    }

    @Test
    void deleteStep_ResourceNotFoundException() throws Exception {
        Long stepId = 999L;

        doThrow(new ResourceNotFoundException("Step not found")).when(stepService).deleteStep(stepId);

        mockMvc.perform(delete("/api/steps/{stepId}", stepId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Step not found"));
    }

    @Test
    void getStepsByQuest_ResourceNotFoundException() throws Exception {
        Long questId = 999L;

        when(stepService.getStepsByQuest(questId))
                .thenThrow(new ResourceNotFoundException("Quest not found"));

        mockMvc.perform(get("/api/steps/quest/{questId}", questId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Quest not found"));
    }
}
