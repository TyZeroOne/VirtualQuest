package org.virtualquest.platform.service;

import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.virtualquest.platform.dto.UpdateUserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Регистрация пользователя
    @Transactional
    public Users registerUser(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new DuplicateRatingException("Username or email already exists");
        }

        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Получение пользователя по ID
    @Transactional(readOnly = true)
    public Optional<Users> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Поиск по username
    @Transactional(readOnly = true)
    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Обновление рейтинга
    @Transactional
    public void updateUserRating(Long userId, int points) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRating(user.getRating() + points);
        userRepository.save(user);
    }

    // Получение топ-N пользователей по рейтингу
    @Transactional(readOnly = true)
    public List<Users> getTopUsers(int limit) {
        return userRepository.findTopNByOrderByRatingDesc(limit);
    }

    // Обновление времени последнего входа
    @Transactional
    public void updateLastLogin(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLastLoginDate(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public Users updateFullName(Long userId, String newFullName) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(newFullName);
        return userRepository.save(user);
    }

    // Обновление логина (username)
    @Transactional
    public Users updateUsername(Long userId, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            throw new DuplicateRatingException("Username already taken");
        }
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    // Обновление email
    @Transactional
    public Users updateEmail(Long userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new DuplicateRatingException("Email already in use");
        }
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    // Обновление пароля
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Полное обновление через DTO
    @Transactional
    public Users updateUser(Long userId, UpdateUserDTO dto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new DuplicateRatingException("Username already taken");
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateRatingException("Email already in use");
            }
            user.setEmail(dto.getEmail());
        }
        return userRepository.save(user);
    }
}