package org.virtualquest.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.virtualquest.platform.service.QuestService;
import org.virtualquest.platform.service.UserService;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
public class ModeratorController {
    private final UserService userService;
    private final QuestService questService;

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        userService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        userService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/disable-reviews")
    public ResponseEntity<Void> disableReviews(@PathVariable Long userId) {
        userService.disableReviews(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/enable-reviews")
    public ResponseEntity<Void> enableReviews(@PathVariable Long userId) {
        userService.enableReviews(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{quest_id}/points")
    public ResponseEntity<Void> setPoints(
            @PathVariable Long quest_id,
            @RequestParam int points
    ) {
        questService.updateQuestPoints(quest_id, points, true);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{quest_id}/disable-auto-calculation")
    public ResponseEntity<Void> disableAutoCalculation(@PathVariable Long quest_id) {
        questService.disableAutoCalculation(quest_id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{quest_id}/enable-auto-calculation")
    public ResponseEntity<Void> enableAutoCalculation(@PathVariable Long quest_id) {
        questService.enableAutoCalculation(quest_id);
        return ResponseEntity.ok().build();
    }
}
