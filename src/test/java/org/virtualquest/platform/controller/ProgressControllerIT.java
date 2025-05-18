package org.virtualquest.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.virtualquest.platform.dto.ProgressDTO;
import org.virtualquest.platform.exception.GlobalExceptionHandler;
import org.virtualquest.platform.filter.JwtAuthFilter;
import org.virtualquest.platform.model.Progress;
import org.virtualquest.platform.service.ProgressService;
import org.virtualquest.platform.util.JwtUtils;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ProgressControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgressService progressService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testStartQuest_Success() throws Exception {
        ProgressDTO dto = new ProgressDTO();
        dto.setUserId(1L);
        dto.setQuestId(2L);

        Progress progress = new Progress();
        progress.setId(1L);

        when(progressService.startQuest(1L, 2L)).thenReturn(progress);

        mockMvc.perform(post("/api/progress/1/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateStep_Success() throws Exception {
        Progress progress = new Progress();
        progress.setId(1L);

        when(progressService.updateStep(1L, 5L)).thenReturn(progress);

        mockMvc.perform(patch("/api/progress/1/step")
                        .param("nextStepId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testCompleteQuest_Success() throws Exception {
        Progress progress = new Progress();
        progress.setId(1L);

        when(progressService.completeQuest(1L)).thenReturn(progress);

        mockMvc.perform(patch("/api/progress/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetProgress_Found() throws Exception {
        Progress progress = new Progress();
        progress.setId(10L);

        when(progressService.getCurrentProgress(1L, 2L)).thenReturn(Optional.of(progress));

        mockMvc.perform(get("/api/progress/2/progress")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testGetProgress_NotFound() throws Exception {
        when(progressService.getCurrentProgress(1L, 2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/progress/2/progress")
                        .param("userId", "1"))
                .andExpect(status().isNotFound());
    }
}
