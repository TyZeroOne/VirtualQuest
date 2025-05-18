package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.virtualquest.platform.model.enums.Difficulty;
import lombok.Data;
import java.util.List;

@Data
public class QuestDTO {
    @NotBlank
    @Size(max = 100)
    private String title;
    @Size(max = 500)
    private String description;
    @NotNull
    private Difficulty difficulty;
    private List<Long> categoryIds;
}