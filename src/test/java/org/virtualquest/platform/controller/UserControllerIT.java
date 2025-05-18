package org.virtualquest.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.virtualquest.platform.dto.UpdateUserDTO;
import org.virtualquest.platform.exception.GlobalExceptionHandler;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.filter.JwtAuthFilter;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.service.UserService;
import org.virtualquest.platform.util.JwtUtils;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void getUser_Success() throws Exception {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("john_doe");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void getUser_NotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTopUsers_Success() throws Exception {
        Users user1 = new Users();
        user1.setId(1L);
        user1.setUsername("user1");
        Users user2 = new Users();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userService.getTopUsers(10)).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")));
    }

    @Test
    void updateFullName_Success() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setFullName("New FullName");

        when(userService.updateFullName(1L, "New FullName")).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1/fullname")
                        .param("newFullName", "New FullName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New FullName"));
    }

    @Test
    void updateUsername_Success() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setUsername("new_username");

        when(userService.updateUsername(1L, "new_username")).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1/username")
                        .param("newUsername", "new_username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new_username"));
    }

    @Test
    void updateEmail_Success() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");

        when(userService.updateEmail(1L, "newemail@example.com")).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1/email")
                        .param("newEmail", "newemail@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }

    @Test
    void updatePassword_Success() throws Exception {
        // updatePassword возвращает void
        doNothing().when(userService).updatePassword(1L, "newPassword123");

        mockMvc.perform(patch("/api/users/1/password")
                        .param("newPassword", "newPassword123"))
                .andExpect(status().isOk());

        verify(userService).updatePassword(1L, "newPassword123");
    }

    @Test
    void updateUser_Success() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFullName("Updated Name");
        dto.setEmail("updated@example.com");
        dto.setUsername("updateduser");

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setFullName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setUsername("updateduser");

        when(userService.updateUser(eq(1L), ArgumentMatchers.<UpdateUserDTO>any())).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    // Тест на исключение, если, например, userService выбрасывает ResourceNotFoundException
    @Test
    void updateUser_NotFound() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setFullName("Name");
        dto.setEmail("email@example.com");
        dto.setUsername("user");

        when(userService.updateUser(eq(999L), ArgumentMatchers.<UpdateUserDTO>any()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

}
