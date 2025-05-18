package org.virtualquest.platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.exception.BusinessLogicException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.repository.ProgressRepository;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.StepRepository;
import org.virtualquest.platform.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProgressServiceTest {
    @Mock
    private ProgressRepository progressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestRepository questRepository;
    @Mock
    private StepRepository stepRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void startQuest_Success() {
        Users user = new Users();
        user.setId(1L);
        Quest quest = new Quest();
        quest.setId(2L);
        quest.setStartedCount(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questRepository.findById(2L)).thenReturn(Optional.of(quest));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(progressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Progress progress = progressService.startQuest(1L, 2L);

        assertEquals(user, progress.getUser());
        assertEquals(quest, progress.getQuest());
        assertNotNull(progress.getStartedAt());
        assertEquals(6, quest.getStartedCount());

        verify(questRepository).save(quest);
        verify(progressRepository).save(progress);
    }

    @Test
    void startQuest_UserNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> progressService.startQuest(1L, 2L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void startQuest_QuestNotFound_Throws() {
        Users user = new Users();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> progressService.startQuest(1L, 2L));
        assertEquals("Quest not found", ex.getMessage());
    }

    @Test
    void updateStep_Success() {
        Progress progress = new Progress();
        Quest quest = new Quest();
        quest.setId(10L);
        progress.setQuest(quest);

        Step nextStep = new Step();
        Quest stepQuest = new Quest();
        stepQuest.setId(10L);
        nextStep.setQuest(stepQuest);

        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findById(2L)).thenReturn(Optional.of(nextStep));
        when(progressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Progress updated = progressService.updateStep(1L, 2L);

        assertEquals(nextStep, updated.getCurrentStep());
        verify(progressRepository).save(updated);
    }

    @Test
    void updateStep_ProgressNotFound_Throws() {
        when(progressRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> progressService.updateStep(1L, 2L));
        assertEquals("Progress not found", ex.getMessage());
    }

    @Test
    void updateStep_StepNotFound_Throws() {
        Progress progress = new Progress();
        Quest quest = new Quest();
        quest.setId(10L);
        progress.setQuest(quest);

        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> progressService.updateStep(1L, 2L));
        assertEquals("Step not found", ex.getMessage());
    }

    @Test
    void updateStep_StepNotBelongToQuest_Throws() {
        Progress progress = new Progress();
        Quest quest = new Quest();
        quest.setId(10L);
        progress.setQuest(quest);

        Step nextStep = new Step();
        Quest otherQuest = new Quest();
        otherQuest.setId(99L);
        nextStep.setQuest(otherQuest);

        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));
        when(stepRepository.findById(2L)).thenReturn(Optional.of(nextStep));

        BusinessLogicException ex = assertThrows(BusinessLogicException.class,
                () -> progressService.updateStep(1L, 2L));
        assertEquals("Step does not belong to the quest", ex.getMessage());
    }

    @Test
    void completeQuest_Success() {
        Progress progress = new Progress();
        Quest quest = new Quest();
        quest.setCompletedCount(3);
        progress.setQuest(quest);

        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(progressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Progress completed = progressService.completeQuest(1L);

        assertTrue(completed.isCompleted());
        assertNotNull(completed.getCompletedAt());
        assertEquals(4, quest.getCompletedCount());

        verify(questRepository).save(quest);
        verify(progressRepository).save(completed);
    }

    @Test
    void completeQuest_ProgressNotFound_Throws() {
        when(progressRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> progressService.completeQuest(1L));
        assertEquals("Progress not found", ex.getMessage());
    }

    @Test
    void getCurrentProgress_Found() {
        Progress progress = new Progress();

        when(progressRepository.findByUserIdAndQuestId(1L, 2L)).thenReturn(Optional.of(progress));

        Optional<Progress> result = progressService.getCurrentProgress(1L, 2L);

        assertTrue(result.isPresent());
        assertEquals(progress, result.get());
    }

    @Test
    void getCurrentProgress_NotFound() {
        when(progressRepository.findByUserIdAndQuestId(1L, 2L)).thenReturn(Optional.empty());

        Optional<Progress> result = progressService.getCurrentProgress(1L, 2L);

        assertFalse(result.isPresent());
    }
}
