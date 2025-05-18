package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StepDTO {
    @NotBlank
    private String description;
    @NotBlank
    private String options;
    private Long nextStepId; // ID следующего шага
}