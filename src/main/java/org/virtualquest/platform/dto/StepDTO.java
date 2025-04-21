package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StepDTO {
    @NotBlank
    private String description;
    @NotBlank
    private String options;
    @NotNull
    private Long nextStepId; // ID следующего шага
}