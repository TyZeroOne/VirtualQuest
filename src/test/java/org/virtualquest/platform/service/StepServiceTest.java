package org.virtualquest.platform.service;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.StepDTO;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StepServiceTest {
    @Mock private StepRepository stepRepository;
    @Mock private QuestRepository questRepository;
    @InjectMocks private StepService stepService;

    @Test
    void createStep_WithNextStep() {
        Quest quest = new Quest();
        quest.setId(1L);
        Step nextStep = new Step();
        nextStep.setId(2L);

        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));
        when(stepRepository.findById(2L)).thenReturn(Optional.of(nextStep));
        when(stepRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StepDTO dto = new StepDTO();
        dto.setNextStepId(2L);
        Step step = stepService.createStep(1L, dto);

        assertEquals(2L, step.getNextStep().getId());
    }

    @Test
    void deleteStep_ClearReferences() {
        Step stepToDelete = new Step();
        stepToDelete.setId(1L);

        Step referencingStep = new Step();
        referencingStep.setNextStep(stepToDelete);

        when(stepRepository.findById(1L)).thenReturn(Optional.of(stepToDelete));
        when(stepRepository.findByNextStepId(1L)).thenReturn(List.of(referencingStep));

        stepService.deleteStep(1L);

        assertNull(referencingStep.getNextStep());
        verify(stepRepository).saveAll(any());
    }
}