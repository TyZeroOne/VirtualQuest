package org.virtualquest.platform.dto;

import lombok.Data;

@Data
public class StepDTO {
    private String description;
    private String options;
    private Long nextStepId; // ID следующего шага
}