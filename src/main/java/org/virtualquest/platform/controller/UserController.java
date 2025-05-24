package org.virtualquest.platform.controller;

import org.virtualquest.platform.dto.UpdateUserDTO;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Получение информации о пользователе
    @GetMapping("/{userId}")
    public ResponseEntity<Users> getUser(@PathVariable Long userId) {
        Optional<Users> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Топ пользователей
    @GetMapping("/top")
    public ResponseEntity<List<Users>> getTopUsers(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(userService.getTopUsers(limit));
    }

    @PatchMapping("/{userId}/fullname")
    public ResponseEntity<Users> updateFullName(
            @PathVariable Long userId,
            @RequestParam String newFullName
    ) {
        return ResponseEntity.ok(
                userService.updateFullName(userId, newFullName)
        );
    }

    @PatchMapping("/{userId}/username")
    public ResponseEntity<Users> updateUsername(
            @PathVariable Long userId,
            @RequestParam String newUsername
    ) {
        return ResponseEntity.ok(
                userService.updateUsername(userId, newUsername)
        );
    }

    @PatchMapping("/{userId}/email")
    public ResponseEntity<Users> updateEmail(
            @PathVariable Long userId,
            @RequestParam String newEmail
    ) {
        return ResponseEntity.ok(
                userService.updateEmail(userId, newEmail)
        );
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long userId,
            @RequestParam String newPassword
    ) {
        userService.updatePassword(userId, newPassword);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Users> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO dto
    ) {
        return ResponseEntity.ok(
                userService.updateUser(userId, dto)
        );
    }

    @GetMapping("/{userId}/rating")
    public ResponseEntity<Integer> getUserRating(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserRating(userId));
    }
}