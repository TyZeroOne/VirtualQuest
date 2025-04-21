package org.virtualquest.platform.controller;

import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Step;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quests")
public class QuestController {
    private final QuestService questService;

    public QuestController(QuestService questService) {
        this.questService = questService;
    }

    // Создание черновика
    @PostMapping("/draft")
    public ResponseEntity<Quest> createDraft(
            @RequestParam Long creatorId,
            @RequestBody QuestDTO dto
    ) {
        return ResponseEntity.ok(questService.createDraft(creatorId, dto));
    }

    // Публикация квеста
    @PatchMapping("/{questId}/publish")
    public ResponseEntity<Quest> publishQuest(@PathVariable Long questId) {
        return ResponseEntity.ok(questService.publishQuest(questId));
    }

    // Добавление шага
    @PostMapping("/{questId}/steps")
    public ResponseEntity<Step> addStep(
            @PathVariable Long questId,
            @RequestParam String description,
            @RequestParam String options
    ) {
        return ResponseEntity.ok(questService.addStep(questId, description, options));
    }

    // Поиск квестов
    @GetMapping("/search")
    public ResponseEntity<List<Quest>> searchQuests(
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) List<Long> categoryIds
    ) {
        return ResponseEntity.ok(questService.findQuests(published, difficulty, categoryIds));
    }
}