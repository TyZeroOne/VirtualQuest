package org.virtualquest.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.virtualquest.platform.dto.QuestCreateDTO;
import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.dto.QuestStatsDTO;
import org.virtualquest.platform.exception.AccessDeniedException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public QuestService(QuestRepository questRepository,
                        UserRepository userRepository,
                        StepRepository stepRepository,
                        CategoryRepository categoryRepository) {
        this.questRepository = questRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Quest> getAllQuests() {
        return questRepository.findAll();
    }

    // Создание квеста (черновик)
    @Transactional
    public Quest createDraft(Long creatorId, QuestCreateDTO dto) {
        Users creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Quest quest = new Quest();
        quest.setTitle(dto.getTitle());
        quest.setDescription(dto.getDescription());
        quest.setDifficulty(dto.getDifficulty());
        quest.setCreator(creator);
        quest.setPublished(false);
        // Добавление категорий
        dto.getCategoryIds().forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            quest.getCategories().add(category);
        });
        return questRepository.save(quest);
    }

    // Публикация квеста
    @Transactional
    public Quest publishQuest(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));
        quest.setPublished(true);
        return questRepository.save(quest);
    }

    // Получение информации о квесте
    public Quest getQuestById(Long questId) {
        return questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));
    }

    // Обновление информации о квесте
    public Quest updateQuest(Long questId, QuestDTO dto) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        if (dto.getTitle() != null) quest.setTitle(dto.getTitle());
        if (dto.getDescription() != null) quest.setDescription(dto.getDescription());
        if (dto.getDifficulty() != null) quest.setDifficulty(dto.getDifficulty());
        return questRepository.save(quest);
    }

    // Добавление категории к квесту
    @Transactional
    public Quest addCategory(Long questId, Long categoryId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        quest.getCategories().add(category);
        return questRepository.save(quest);
    }

    @Transactional
    public void deleteQuest(Long questId, Long requesterId, String role) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        if (role.equals("ROLE_MODERATOR") || role.equals("ROLE_ADMIN")) {
            questRepository.delete(quest);
        }
        else if (quest.getCreator().getId().equals(requesterId)) {
            questRepository.delete(quest);
        } else {
            throw new AccessDeniedException("Only quest creator");
        }
    }

    // Поиск квестов по фильтрам
    public List<Quest> findQuests(boolean published, Difficulty difficulty, List<Long> categoryIds) {
        return questRepository.findByFilters(published, difficulty, categoryIds);
    }

    @Transactional
    public void updateQuestPoints(Long questId, int points, boolean isManual) {

        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isManual && !quest.isEditable() && !isAdmin) {
            throw new IllegalStateException("Editing points is disabled for this quest");
        }

        quest.setPoints(points);
        quest.calculateDifficulty();
        quest.setRatingConsidered(true);
        quest.setAutoCalculated(!isManual);
        questRepository.save(quest);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Ежедневный расчет
    public void recalculatePopularQuests() {
        List<Quest> popularQuests = questRepository.findByStartedCountGreaterThanEqual(100, true);
        popularQuests.forEach(Quest::calculateAutoPoints);
        questRepository.saveAll(popularQuests);
    }

    // Увеличение счетчика начавших квест
    public void incrementStartedCount(Long questId) {
        int updated = questRepository.incrementStartedCount(questId);
        if (updated == 0) {
            throw new ResourceNotFoundException("Quest not found");
        }
    }

    @Transactional
    public void toggleQuestEditable(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        quest.setEditable(!quest.isEditable());
        questRepository.save(quest);
    }

    @Transactional
    public void disableAutoCalculation(Long questId) {
        Quest quest = getQuestById(questId);
        quest.setAutoCalculated(false);
        questRepository.save(quest);
    }

    @Transactional
    public void enableAutoCalculation(Long questId) {
        Quest quest = getQuestById(questId);
        quest.setAutoCalculated(true);
        questRepository.save(quest);
    }

    public QuestStatsDTO getQuestStats(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        return new QuestStatsDTO(
                quest.getStartedCount(),
                quest.getCompletedCount()
        );
    }
}