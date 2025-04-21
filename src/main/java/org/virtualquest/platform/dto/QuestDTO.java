package org.virtualquest.platform.dto;

import org.virtualquest.platform.model.enums.Difficulty;
import lombok.Data;
import java.util.List;

@Data
public class QuestDTO {
    private String title;
    private String description;
    private Difficulty difficulty;
    private List<Long> categoryIds;
}