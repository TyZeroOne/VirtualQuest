package org.virtualquest.platform.dto;

import lombok.Data;

@Data
public class ChangeRoleRequestDTO {
    private Long userId;
    private String role;
}
