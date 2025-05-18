package org.virtualquest.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.StepDTO;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Step;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.StepRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StepServiceTest {

    private StepRepository stepRepository;
    private QuestRepository questRepository;
    private StepService stepService;

    @BeforeEach
    void setUp() {
        stepRepository = mock(StepRepository.class);
        questRepository = mock(QuestRepository.class);
        stepService = new StepService(stepRepository, questRepository);
    }

    // --- createStep ---

    @Test
    void testCreateStepSuccess() {
        Long questId = 1L;
        StepDTO dto = new StepDTO("description", "option1", null);
        Quest quest = new Quest();
        quest.setId(questId);
        quest.setSteps(List.of()); // empty list

        when(questRepository.findByIdWithSteps(questId)).thenReturn(Optional.of(quest));
        when(stepRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.createStep(questId, dto);

        assertEquals("description", result.getDescription());
        assertEquals("option1", result.getOptions());
        assertEquals(1, result.getStepNumber());
        assertNull(result.getNextStep());
    }

    @Test
    void testCreateStepWithNextStep() {
        Long questId = 1L;
        Long nextStepId = 2L;

        StepDTO dto = new StepDTO("desc", "opt", nextStepId);
        Quest quest = new Quest();
        quest.setSteps(List.of(new Step())); // 1 existing step

        Step nextStep = new Step();
        nextStep.setId(nextStepId);

        when(questRepository.findByIdWithSteps(questId)).thenReturn(Optional.of(quest));
        when(stepRepository.findById(nextStepId)).thenReturn(Optional.of(nextStep));
        when(stepRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.createStep(questId, dto);

        assertEquals(nextStep, result.getNextStep());
        assertEquals(2, result.getStepNumber()); // existing step + 1
    }

    @Test
    void testCreateStepQuestNotFound() {
        when(questRepository.findByIdWithSteps(1L)).thenReturn(Optional.empty());

        StepDTO dto = new StepDTO("desc", "opt", null);
        assertThrows(ResourceNotFoundException.class, () -> stepService.createStep(1L, dto));
    }

    @Test
    void testCreateStepNextStepNotFound() {
        Quest quest = new Quest();
        quest.setSteps(List.of());

        when(questRepository.findByIdWithSteps(1L)).thenReturn(Optional.of(quest));
        when(stepRepository.findById(999L)).thenReturn(Optional.empty());

        StepDTO dto = new StepDTO("desc", "opt", 999L);
        assertThrows(ResourceNotFoundException.class, () -> stepService.createStep(1L, dto));
    }

    // --- updateStep ---

    @Test
    void testUpdateStepSuccess() {
        Step step = new Step();
        step.setId(1L);
        step.setDescription("old");
        step.setOptions("old");

        Step next = new Step();
        next.setId(2L);

        StepDTO dto = new StepDTO("newDesc", "newOpt", 2L);

        when(stepRepository.findById(1L)).thenReturn(Optional.of(step));
        when(stepRepository.findById(2L)).thenReturn(Optional.of(next));
        when(stepRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStep(1L, dto);

        assertEquals("newDesc", result.getDescription());
        assertEquals("newOpt", result.getOptions());
        assertEquals(next, result.getNextStep());
    }

    @Test
    void testUpdateStepNotFound() {
        when(stepRepository.findById(1L)).thenReturn(Optional.empty());

        StepDTO dto = new StepDTO("desc", "opt", null);
        assertThrows(ResourceNotFoundException.class, () -> stepService.updateStep(1L, dto));
    }

    @Test
    void testUpdateStepNextStepNotFound() {
        Step step = new Step();
        step.setId(1L);

        when(stepRepository.findById(1L)).thenReturn(Optional.of(step));
        when(stepRepository.findById(99L)).thenReturn(Optional.empty());

        StepDTO dto = new StepDTO("desc", "opt", 99L);
        assertThrows(ResourceNotFoundException.class, () -> stepService.updateStep(1L, dto));
    }

    @Test
    void testUpdateStepNextStepNull() {
        Step step = new Step();
        step.setId(1L);
        step.setNextStep(new Step());

        StepDTO dto = new StepDTO(null, null, null); // remove next step

        when(stepRepository.findById(1L)).thenReturn(Optional.of(step));
        when(stepRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Step result = stepService.updateStep(1L, dto);
        assertNull(result.getNextStep());
    }

    // --- deleteStep ---

    @Test
    void testDeleteStepSuccess() {
        Step step = new Step();
        step.setId(1L);

        Step ref1 = new Step();
        Step ref2 = new Step();
        ref1.setNextStep(step);
        ref2.setNextStep(step);

        when(stepRepository.findById(1L)).thenReturn(Optional.of(step));
        when(stepRepository.findByNextStepId(1L)).thenReturn(List.of(ref1, ref2));

        stepService.deleteStep(1L);

        verify(stepRepository).saveAll(anyList());
        verify(stepRepository).delete(step);
    }

    @Test
    void testDeleteStepNotFound() {
        when(stepRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stepService.deleteStep(1L));
    }

    // --- getStepsByQuest ---

    @Test
    void testGetStepsByQuest() {
        Step s1 = new Step(); s1.setId(1L);
        Step s2 = new Step(); s2.setId(2L);

        when(stepRepository.findByQuestId(5L)).thenReturn(List.of(s1, s2));

        List<Step> result = stepService.getStepsByQuest(5L);
        assertEquals(2, result.size());
    }
}
