package org.virtualquest.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Quest>> recommendForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Quest> recommendations = recommendationService.recommendQuestsForUser(userId, limit);
        return ResponseEntity.ok(recommendations);
    }
}
