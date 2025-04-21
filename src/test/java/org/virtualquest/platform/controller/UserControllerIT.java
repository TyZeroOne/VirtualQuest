package org.virtualquest.platform.controller;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.UpdateUserDTO;
import org.virtualquest.platform.model.Users;
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
class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .param("username", "newuser")
                        .param("email", "new@mail.com")
                        .param("password", "pass123")
                        .param("fullName", "New User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void updateUser_ValidData() throws Exception {
        Users user = new Users();
        user.setUsername("olduser");
        user.setEmail("old@mail.com");
        user.setPassword("pass");
        user.setFullName("Old Name");
        Users save_user = userRepository.save(user);

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername("newuser");
        dto.setFullName("New Name");

        mockMvc.perform(put("/api/users/" + save_user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\", \"fullName\":\"New Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));
    }
}