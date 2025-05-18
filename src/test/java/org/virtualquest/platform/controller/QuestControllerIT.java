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
import org.virtualquest.platform.dto.QuestCreateDTO;
import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.dto.QuestStatsDTO;
import org.virtualquest.platform.exception.GlobalExceptionHandler;
import org.virtualquest.platform.filter.JwtAuthFilter;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Role;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.UserRepository;
import org.virtualquest.platform.service.CategoryService;
import org.virtualquest.platform.service.QuestService;
import org.virtualquest.platform.util.JwtUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class QuestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestService questService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDraft() throws Exception {
        QuestCreateDTO dto = new QuestCreateDTO();
        dto.setCreatorId(1L);

        Quest quest = new Quest();
        quest.setId(10L);

        when(questService.createDraft(1L, dto)).thenReturn(quest);

        mockMvc.perform(post("/api/quests/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testGetQuest() throws Exception {
        Quest quest = new Quest();
        quest.setId(10L);

        when(questService.getQuestById(10L)).thenReturn(quest);

        mockMvc.perform(get("/api/quests/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void testUpdateQuest() throws Exception {
        QuestDTO dto = new QuestDTO();
        dto.setTitle("Test Quest Title");
        dto.setDifficulty(Difficulty.EASY);
        dto.setDescription("Описание квеста");
        dto.setCategoryIds(List.of(1L, 2L));

        Quest quest = new Quest();
        quest.setId(99L);

        when(questService.updateQuest(99L, dto)).thenReturn(quest);

        mockMvc.perform(put("/api/quests/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L));
    }

    @Test
    void testGetQuestStats_Success() throws Exception {
        long questId = 1L;
        QuestStatsDTO statsDTO = new QuestStatsDTO(10L, 5L);

        when(questService.getQuestStats(questId)).thenReturn(statsDTO);

        mockMvc.perform(get("/api/quests/{questId}/stats", questId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.started").value(statsDTO.started()))
                .andExpect(jsonPath("$.completed").value(statsDTO.completed()));

        verify(questService).getQuestStats(questId);
    }

    @Test
    void testPublishQuest() throws Exception {
        Quest quest = new Quest();
        quest.setId(12L);

        when(questService.publishQuest(12L)).thenReturn(quest);

        mockMvc.perform(patch("/api/quests/12/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12L));
    }

    @Test
    void testDeleteQuest_Success() throws Exception {
        Long questId = 1L;
        Long requesterId = 2L;

        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        Users user = new Users();
        user.setId(requesterId);
        user.setRoles(role);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));
        doNothing().when(questService).deleteQuest(questId, requesterId, "ROLE_USER");

        mockMvc.perform(delete("/api/quests/{questId}", questId)
                        .header("requesterId", requesterId))
                .andExpect(status().isNoContent());

        verify(userRepository).findById(requesterId);
        verify(questService).deleteQuest(questId, requesterId, "ROLE_USER");
    }

    @Test
    void testDeleteQuest_UserNotFound() throws Exception {
        Long questId = 1L;
        Long requesterId = 999L;

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/quests/{questId}", questId)
                        .header("requesterId", requesterId))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(requesterId);
        verify(questService, never()).deleteQuest(anyLong(), anyLong(), anyString());
    }

}
