package org.virtualquest.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.virtualquest.platform.dto.*;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.Role;
import org.virtualquest.platform.repository.RoleRepository;
import org.virtualquest.platform.repository.UserRepository;
import org.virtualquest.platform.security.UserDetailsServiceImpl;
import org.virtualquest.platform.service.UserService;
import org.virtualquest.platform.util.JwtUtils;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Username or Email already taken.");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setCreatedAt(LocalDateTime.now());

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        user.setRoles(userRole);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        // Авторизация пользователя
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Установка аутентификации в SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Получение пользователя из аутентификации
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Обновление даты последнего входа через сервис
        userService.updateLastLoginDate(username);

        // Генерация access токена
        String accessToken = jwtUtils.generateAccessToken((UserDetails) authentication.getPrincipal());

        // Генерация refresh токена
        String refreshToken = jwtUtils.generateRefreshToken((UserDetails) authentication.getPrincipal());

        // Ответ с токенами
        return ResponseEntity.ok(new TokenRefreshResponseDTO(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtils.validateJwtToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String username = jwtUtils.getUsernameFromJwtToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails); // по желанию

        return ResponseEntity.ok(new TokenRefreshResponseDTO(newAccessToken, newRefreshToken));
    }
}
