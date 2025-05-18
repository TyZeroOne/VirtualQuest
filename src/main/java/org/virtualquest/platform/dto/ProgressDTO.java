package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgressDTO {
    @NotNull
    private Long userId;
    @NotNull
    private Long questId;
}
