package org.virtualquest.platform.controller;

import org.virtualquest.platform.dto.RatingDTO;
import org.virtualquest.platform.model.Rating;
import org.virtualquest.platform.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // Добавить оценку
    @PostMapping
    public ResponseEntity<Rating> addRating(
            @RequestParam Long userId,
            @RequestParam Long questId,
            @RequestBody RatingDTO dto
    ) {
        return ResponseEntity.ok(ratingService.addRating(userId, questId, dto));
    }

    // Получить оценки квеста
    @GetMapping("/quest/{questId}")
    public ResponseEntity<List<Rating>> getQuestRatings(@PathVariable Long questId) {
        return ResponseEntity.ok(ratingService.getRatingsByQuest(questId));
    }

    // Удалить оценку
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long ratingId,
            @RequestParam Long userId
    ) {
        ratingService.deleteRating(ratingId, userId);
        return ResponseEntity.noContent().build();
    }

    // Средний рейтинг квеста
    @GetMapping("/quest/{questId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long questId) {
        return ResponseEntity.ok(ratingService.getAverageRating(questId));
    }
}