package org.virtualquest.platform.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String username;
    private String email;
    private String fullName;
    private String newPassword;
}