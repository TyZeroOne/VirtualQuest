package org.virtualquest.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.virtualquest.platform.dto.ChangeRoleRequestDTO;
import org.virtualquest.platform.service.UserService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')") // Доступ только для администраторов
    @PostMapping("/change-role")
    public ResponseEntity<?> changeUserRole(@RequestBody ChangeRoleRequestDTO request) {
        try {
            userService.changeUserRole(request.getUserId(), request.getRole());
            return ResponseEntity.ok("Role changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
