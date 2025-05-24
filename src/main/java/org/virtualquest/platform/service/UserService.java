package org.virtualquest.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Role;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.RoleRepository;
import org.virtualquest.platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.virtualquest.platform.dto.UpdateUserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    // Получение пользователя по ID
    public Optional<Users> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Поиск по username
    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username);
    }

    public void updateLastLoginDate(String username) {
        Optional<Users> optionalUser = userRepository.findByUsernameAndDeletedFalse(username);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
    }

    @Transactional
    public void softDeleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setDeleted(true);
        user.setEmail(generateRandomizedEmail(user.getEmail()));
        user.setUsername(generateRandomizedUsername(user.getUsername()));

        userRepository.save(user);
    }

    private String generateRandomizedEmail(String originalEmail) {
        String uniqueSuffix = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String domain = originalEmail.contains("@") ? originalEmail.substring(originalEmail.indexOf("@")) : "@deleted.com";
        return "deleted_" + uniqueSuffix + domain;
    }

    private String generateRandomizedUsername(String originalUsername) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "deleted_user_" + suffix;
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

    public void changeUserRole(Long userId, String roleName) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role.RoleName roleEnum = Role.RoleName.valueOf(roleName.toUpperCase());
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRoles(role);

        userRepository.save(user);
    }

    public Integer getUserRating(Long userId) {
        Integer rating = userRepository.calculateUserRatingFromCompletedQuests(userId);
        return rating != null ? rating : 0;
    }

    // Получение топ-N пользователей по рейтингу
    public List<Users> getTopUsers(int limit) {
        List<Users> users = userRepository.findAllWithCompletedQuests();

        return users.stream()
                .map(u -> Map.entry(u, getUserRating(u.getId())))
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Transactional
    public void banUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void disableReviews(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setCanPostReviews(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableReviews(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setCanPostReviews(true);
        userRepository.save(user);
    }
}