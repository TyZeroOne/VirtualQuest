package org.virtualquest.platform.service;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProgressServiceTest {
    @Mock private ProgressRepository progressRepository;
    @Mock private UserRepository userRepository;
    @Mock private QuestRepository questRepository;
    @Mock private StepRepository stepRepository;
    @InjectMocks private ProgressService progressService;

    @Test
    void startQuest_ValidData() {
        Users user = new Users();
        Quest quest = new Quest();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));
        when(progressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Progress progress = progressService.startQuest(1L, 1L);
        assertNotNull(progress.getStartedAt());
    }

    @Test
    void completeQuest_Success() {
        Progress progress = new Progress();
        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Progress result = progressService.completeQuest(1L);
        assertTrue(result.isCompleted());
        assertNotNull(result.getCompletedAt());
    }
}