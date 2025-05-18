package org.virtualquest.platform.dto;

import lombok.Data;

@Data
public class TokenRefreshRequestDTO {
    private String refreshToken;
}