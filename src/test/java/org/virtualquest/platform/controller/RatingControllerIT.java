package org.virtualquest.platform.controller;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.*;
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
class RatingControllerIT {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private QuestRepository questRepository;

    @Test
    void addRating_ValidData() throws Exception {
        Users user_ = new Users();
        user_.setUsername("user");
        user_.setEmail("user@mail.com");
        user_.setPassword("pass");
        user_.setFullName("User");
        Users user = userRepository.save(user_);

        Quest quest_ = new Quest();
        quest_.setTitle("Test Quest");
        quest_.setDifficulty(Difficulty.HARD);
        quest_.setCreator(user);
        Quest quest = questRepository.save(quest_);

        mockMvc.perform(post("/api/ratings")
                        .param("userId", user.getId().toString())
                        .param("questId", quest.getId().toString())
                        .contentType("application/json")
                        .content("{\"rating\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5));
    }
}