package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingRequestDTO {
    @NotNull
    private Long userId;
    @NotNull
    private Long questId;
    @NotNull
    private RatingDTO rating;
}
