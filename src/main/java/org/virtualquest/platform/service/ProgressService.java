package org.virtualquest.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.virtualquest.platform.exception.BusinessLogicException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.repository.ProgressRepository;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.StepRepository;
import org.virtualquest.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ProgressService {
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final StepRepository stepRepository;

    public ProgressService(ProgressRepository progressRepository,
                           UserRepository userRepository,
                           QuestRepository questRepository,
                           StepRepository stepRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.questRepository = questRepository;
        this.stepRepository = stepRepository;
    }

    // Начать квест
    @Transactional
    public Progress startQuest(Long userId, Long questId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        Progress progress = new Progress();
        progress.setUser(user);
        progress.setQuest(quest);
        progress.setStartedAt(LocalDateTime.now());

        // Увеличиваем счетчик начавших
        quest.setStartedCount(quest.getStartedCount() + 1);
        questRepository.save(quest);

        return progressRepository.save(progress);
    }

    // Обновить текущий шаг
    @Transactional
    public Progress updateStep(Long progressId, Long nextStepId) {
        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
        Step nextStep = stepRepository.findById(nextStepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found"));

        if (!nextStep.getQuest().getId().equals(progress.getQuest().getId())) {
            throw new BusinessLogicException("Step does not belong to the quest");
        }

        progress.setCurrentStep(nextStep);
        return progressRepository.save(progress);
    }

    // Завершить квест
    @Transactional
    public Progress completeQuest(Long progressId) {
        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        Quest quest = progress.getQuest();
        quest.setCompletedCount(quest.getCompletedCount() + 1);
        questRepository.save(quest);

        return progressRepository.save(progress);
    }

    // Получить текущий прогресс
    public Optional<Progress> getCurrentProgress(Long userId, Long questId) {
        return progressRepository.findByUserIdAndQuestId(userId, questId);
    }
}