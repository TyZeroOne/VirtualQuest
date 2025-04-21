package org.virtualquest.platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "Password must contain at least 8 characters with letters and numbers")
    private String password;

    @NotBlank
    private String fullName;
}