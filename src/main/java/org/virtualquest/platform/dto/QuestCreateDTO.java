package org.virtualquest.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.virtualquest.platform.model.enums.Difficulty;

import java.util.List;

@Data
public class QuestCreateDTO {
    @NotNull
    private Long creatorId;
    @NotBlank
    @Size(max = 100)
    private String title;
    @Size(max = 500)
    private String description;
    @NotNull
    private Difficulty difficulty;
    @NotEmpty
    private List<Long> categoryIds;
}
