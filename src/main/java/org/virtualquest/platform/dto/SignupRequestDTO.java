package org.virtualquest.platform.dto;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String username;
    private String email;
    private String password;
    private String fullName;
}
