package org.virtualquest.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.UpdateUserDTO;
import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Role;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.RoleRepository;
import org.virtualquest.platform.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        roleRepository = mock(RoleRepository.class);
        userService = new UserService(userRepository, passwordEncoder, roleRepository);
    }

    @Test
    void testUpdateUsername_Success() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("old");

        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0)); // <-- тоже важно

        Users result = userService.updateUsername(1L, "new");

        assertNotNull(result);
        assertEquals("new", result.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUsername_Duplicate() {
        when(userRepository.existsByUsername("new")).thenReturn(true);

        assertThrows(DuplicateRatingException.class, () -> userService.updateUsername(1L, "new"));
    }

    @Test
    void testUpdateUsername_UserNotFound() {
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUsername(1L, "new"));
    }

    void testUpdateEmail_Success() {
        // Подготовка данных
        Long userId = 1L;
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        Users user = new Users();
        user.setId(userId);
        user.setEmail(oldEmail);

        // Моки
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Вызов сервиса
        Users result = userService.updateEmail(userId, newEmail);

        // Проверка
        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateEmail_Duplicate() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(DuplicateRatingException.class, () -> userService.updateEmail(1L, "taken@example.com"));
    }

    @Test
    void testUpdatePassword_Success() {
        Users user = new Users();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");

        userService.updatePassword(1L, "newpass");
        assertEquals("encoded", user.getPassword());
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updatePassword(1L, "newpass"));
    }

    @Test
    void testUpdateUser_Success() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("old");
        user.setEmail("old@old.com");

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername("new");
        dto.setEmail("new@new.com");
        dto.setFullName("Full");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.existsByEmail("new@new.com")).thenReturn(false);
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0)); // <-- это важно

        Users result = userService.updateUser(1L, dto);

        assertNotNull(result);
        assertEquals("new", result.getUsername());
        assertEquals("new@new.com", result.getEmail());
        assertEquals("Full", result.getFullName());
    }

    @Test
    void testUpdateUser_DuplicateUsername() {
        Users user = new Users();
        user.setUsername("old");

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername("new");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("new")).thenReturn(true);

        assertThrows(DuplicateRatingException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void testUpdateUser_DuplicateEmail() {
        Users user = new Users();
        user.setEmail("old@old.com");

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail("new@new.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@new.com")).thenReturn(true);

        assertThrows(DuplicateRatingException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void testChangeUserRole_Success() {
        Users user = new Users();
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_MODERATOR);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_MODERATOR)).thenReturn(Optional.of(role));

        userService.changeUserRole(1L, "ROLE_MODERATOR");
        assertEquals(role, user.getRoles());
    }

    @Test
    void testChangeUserRole_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeUserRole(1L, "ROLE_USER"));
    }

    @Test
    void testChangeUserRole_RoleNotFound() {
        Users user = new Users();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeUserRole(1L, "ROLE_ADMIN"));
    }

    @Test
    void testUpdateLastLoginDate_UserExists() {
        Users user = new Users();
        user.setUsername("test");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        userService.updateLastLoginDate("test");

        assertNotNull(user.getLastLoginDate());
    }

    @Test
    void testUpdateLastLoginDate_UserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateLastLoginDate("missing"));
    }
}
