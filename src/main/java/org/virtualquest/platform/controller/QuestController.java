package org.virtualquest.platform.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.virtualquest.platform.dto.QuestCreateDTO;
import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.dto.QuestStatsDTO;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.UserRepository;
import org.virtualquest.platform.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quests")
public class QuestController {
    private final QuestService questService;
    private final UserRepository userRepository;

    public QuestController(QuestService questService, UserRepository userRepository) {
        this.questService = questService;
        this.userRepository = userRepository;
    }

    // Вывод всех имеющихся квестов
    @GetMapping("/quests")
    public ResponseEntity<List<Quest>> getAllQuests() {
        List<Quest> quests = questService.getAllQuests();
        return ResponseEntity.ok(quests);
    }

    // Создание черновика
    @PostMapping("/draft")
    public ResponseEntity<Quest> createDraft(
            @RequestBody QuestCreateDTO dto
    ) {
        return ResponseEntity.ok(questService.createDraft(dto.getCreatorId(), dto));
    }

    // Получение квеста по ID
    @GetMapping("/{questId}")
    public ResponseEntity<Quest> getQuest(
            @PathVariable Long questId
    ) {
        return ResponseEntity.ok(questService.getQuestById(questId));
    }

    // Обновление квеста
    @PutMapping("/{questId}")
    public ResponseEntity<Quest> updateQuest(
            @PathVariable Long questId,
            @Valid @RequestBody QuestDTO dto
    ) {
        return ResponseEntity.ok(questService.updateQuest(questId, dto));
    }

    @GetMapping("/{questId}/stats")
    public ResponseEntity<QuestStatsDTO> getQuestStats(@PathVariable Long questId) {
        return ResponseEntity.ok(questService.getQuestStats(questId));
    }

    // Публикация квеста
    @PatchMapping("/{questId}/publish")
    public ResponseEntity<Quest> publishQuest(@PathVariable Long questId) {
        return ResponseEntity.ok(questService.publishQuest(questId));
    }

    // Поиск квестов
    @GetMapping("/search")
    public ResponseEntity<List<Quest>> searchQuests(
            @RequestParam(required = false) boolean published,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) List<Long> categoryIds
    ) {
        return ResponseEntity.ok(questService.findQuests(published, difficulty, categoryIds));
    }

    // Удаление квеста
    @DeleteMapping("/{questId}")
    public ResponseEntity<Void> deleteQuest(
            @PathVariable Long questId,
            @RequestHeader Long requesterId
    ) {
        Users requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Получаем роль пользователя в виде строки
        String role = requester.getRoles().getName().name();

        // Передаем ID квеста, ID пользователя и его роль в сервис
        questService.deleteQuest(questId, requesterId, role);

        return ResponseEntity.noContent().build();
    }

}