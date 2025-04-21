package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 200)
    private String description;
}