package org.virtualquest.platform.controller;

import org.virtualquest.platform.dto.ProgressDTO;
import org.virtualquest.platform.model.Progress;
import org.virtualquest.platform.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // Начать квест
    @PostMapping("/{progressId}/start")
    public ResponseEntity<Progress> startQuest(
            @RequestBody ProgressDTO dto
    ) {
        return ResponseEntity.ok(progressService.startQuest(dto.getUserId(), dto.getQuestId()));
    }

    // Обновить шаг
    @PatchMapping("/{progressId}/step")
    public ResponseEntity<Progress> updateStep(
            @PathVariable Long progressId,
            @RequestParam Long nextStepId
    ) {
        return ResponseEntity.ok(progressService.updateStep(progressId, nextStepId));
    }

    // Завершить квест
    @PatchMapping("/{progressId}/complete")
    public ResponseEntity<Progress> completeQuest(@PathVariable Long progressId) {
        return ResponseEntity.ok(progressService.completeQuest(progressId));
    }

    // Получить текущий прогресс
    @GetMapping("/{questId}/progress")
    public ResponseEntity<Progress> getProgress(
            @RequestParam Long userId,
            @PathVariable Long questId
    ) {
        Optional<Progress> progress = progressService.getCurrentProgress(userId, questId);
        return progress.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}