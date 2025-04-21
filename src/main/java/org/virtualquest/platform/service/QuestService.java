package org.virtualquest.platform.service;

import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final StepRepository stepRepository;
    private final CategoryRepository categoryRepository;

    public QuestService(QuestRepository questRepository,
                        UserRepository userRepository,
                        StepRepository stepRepository,
                        CategoryRepository categoryRepository) {
        this.questRepository = questRepository;
        this.userRepository = userRepository;
        this.stepRepository = stepRepository;
        this.categoryRepository = categoryRepository;
    }

    // Создание квеста (черновик)
    @Transactional
    public Quest createDraft(Long creatorId, QuestDTO dto) {
        Users creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Quest quest = new Quest();
        quest.setTitle(dto.getTitle());
        quest.setDescription(dto.getDescription());
        quest.setDifficulty(dto.getDifficulty());
        quest.setCreator(creator);
        quest.setPublished(false);
        return questRepository.save(quest);
    }

    // Публикация квеста
    @Transactional
    public Quest publishQuest(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found"));
        quest.setPublished(true);
        return questRepository.save(quest);
    }

    // Добавление шага к квесту
    @Transactional
    public Step addStep(Long questId, String description, String options) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found"));

        Step step = new Step();
        step.setQuest(quest);
        step.setDescription(description);
        step.setOptions(options);
        step.setStepNumber(quest.getSteps().size() + 1);
        return stepRepository.save(step);
    }

    // Удаление шага
    @Transactional
    public void deleteStep(Long stepId) {
        stepRepository.deleteById(stepId);
    }

    // Обновление информации о квесте
    @Transactional
    public Quest updateQuest(Long questId, QuestDTO dto) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found"));

        if (dto.getTitle() != null) quest.setTitle(dto.getTitle());
        if (dto.getDescription() != null) quest.setDescription(dto.getDescription());
        if (dto.getDifficulty() != null) quest.setDifficulty(dto.getDifficulty());
        return questRepository.save(quest);
    }

    // Добавление категории к квесту
    @Transactional
    public Quest addCategory(Long questId, Long categoryId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        quest.getCategories().add(category);
        return questRepository.save(quest);
    }

    // Поиск квестов по фильтрам
    @Transactional(readOnly = true)
    public List<Quest> findQuests(boolean published, Difficulty difficulty, List<Long> categoryIds) {
        return questRepository.findByFilters(published, difficulty, categoryIds);
    }

    // Увеличение счетчика начавших квест
    @Transactional
    public void incrementStartedCount(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found"));
        quest.setStartedCount(quest.getStartedCount() + 1);
        questRepository.save(quest);
    }
}