package org.virtualquest.platform.service;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Users user = userService.registerUser("test", "test@mail.com", "pass123", "Test User");

        assertEquals("encodedPass", user.getPassword());
        assertEquals("Test User", user.getFullName());
        verify(userRepository).save(any());
    }

    @Test
    void updateRating_ValidUser() {
        Users user = new Users();
        user.setRating(100);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserRating(1L, 50);

        assertEquals(150, user.getRating());
        verify(userRepository).save(user);
    }
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void updateUsername_Conflict() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new Users()));

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.updateUsername(1L, "newuser");
        });
    }
}