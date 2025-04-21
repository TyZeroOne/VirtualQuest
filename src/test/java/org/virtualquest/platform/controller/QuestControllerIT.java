package org.virtualquest.platform.controller;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.UserRepository;
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
class QuestControllerIT {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private QuestRepository questRepository;

    @Test
    void createDraft_ReturnsCreated() throws Exception {
        Users user = new Users();
        user.setUsername("creator");
        user.setEmail("creator@mail.com");
        user.setPassword("pass");
        user.setFullName("Creator");
        Users creator = userRepository.save(user);

        mockMvc.perform(post("/api/quests/draft")
                        .param("creatorId", creator.getId().toString())
                        .contentType("application/json")
                        .content("{\"title\":\"New Quest\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Quest"));
    }
}