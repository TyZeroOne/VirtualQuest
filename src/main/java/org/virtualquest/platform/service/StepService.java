package org.virtualquest.platform.service;

import org.virtualquest.platform.dto.StepDTO;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.StepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StepService {
    private final StepRepository stepRepository;
    private final QuestRepository questRepository;

    public StepService(StepRepository stepRepository, QuestRepository questRepository) {
        this.stepRepository = stepRepository;
        this.questRepository = questRepository;
    }

    // Создать шаг
    @Transactional
    public Step createStep(Long questId, StepDTO dto) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found"));

        Step step = new Step();
        step.setQuest(quest);
        step.setDescription(dto.getDescription());
        step.setOptions(dto.getOptions());
        step.setStepNumber(quest.getSteps().size() + 1);

        // Установка следующего шага (если указан)
        if (dto.getNextStepId() != null) {
            Step nextStep = stepRepository.findById(dto.getNextStepId())
                    .orElseThrow(() -> new IllegalArgumentException("Next step not found"));
            step.setNextStep(nextStep);
        }

        return stepRepository.save(step);
    }

    // Обновить шаг
    @Transactional
    public Step updateStep(Long stepId, StepDTO dto) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Step not found"));

        if (dto.getDescription() != null) {
            step.setDescription(dto.getDescription());
        }
        if (dto.getOptions() != null) {
            step.setOptions(dto.getOptions());
        }
        if (dto.getNextStepId() != null) {
            Step nextStep = stepRepository.findById(dto.getNextStepId())
                    .orElseThrow(() -> new IllegalArgumentException("Next step not found"));
            step.setNextStep(nextStep);
        }

        return stepRepository.save(step);
    }

    // Удалить шаг
    @Transactional
    public void deleteStep(Long stepId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Step not found"));

        // Обнулить ссылки на удаляемый шаг у других шагов
        List<Step> referencingSteps = stepRepository.findByNextStepId(stepId);
        referencingSteps.forEach(s -> s.setNextStep(null));
        stepRepository.saveAll(referencingSteps);

        stepRepository.delete(step);
    }

    // Получить все шаги квеста
    @Transactional(readOnly = true)
    public List<Step> getStepsByQuest(Long questId) {
        return stepRepository.findByQuestId(questId);
    }
}