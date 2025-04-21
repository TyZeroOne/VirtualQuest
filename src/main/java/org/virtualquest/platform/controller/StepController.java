package org.virtualquest.platform.controller;

import org.virtualquest.platform.dto.StepDTO;
import org.virtualquest.platform.model.Step;
import org.virtualquest.platform.service.StepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/steps")
public class StepController {
    private final StepService stepService;

    public StepController(StepService stepService) {
        this.stepService = stepService;
    }

    // Создать шаг
    @PostMapping("/quest/{questId}")
    public ResponseEntity<Step> createStep(
            @PathVariable Long questId,
            @RequestBody StepDTO dto
    ) {
        return ResponseEntity.ok(stepService.createStep(questId, dto));
    }

    // Обновить шаг
    @PutMapping("/{stepId}")
    public ResponseEntity<Step> updateStep(
            @PathVariable Long stepId,
            @RequestBody StepDTO dto
    ) {
        return ResponseEntity.ok(stepService.updateStep(stepId, dto));
    }

    // Удалить шаг
    @DeleteMapping("/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long stepId) {
        stepService.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }

    // Получить шаги квеста
    @GetMapping("/quest/{questId}")
    public ResponseEntity<List<Step>> getStepsByQuest(@PathVariable Long questId) {
        return ResponseEntity.ok(stepService.getStepsByQuest(questId));
    }
}