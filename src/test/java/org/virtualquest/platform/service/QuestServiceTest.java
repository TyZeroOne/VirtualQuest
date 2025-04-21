package org.virtualquest.platform.service;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class QuestServiceTest {
    @Mock private QuestRepository questRepository;
    @Mock private UserRepository userRepository;
    @Mock private StepRepository stepRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private QuestService questService;

    @Test
    void createDraft_ValidData() {
        Users creator = new Users();
        creator.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        QuestDTO dto = new QuestDTO();
        dto.setTitle("Test Quest");
        Quest quest = questService.createDraft(1L, dto);

        assertFalse(quest.isPublished());
        assertEquals("Test Quest", quest.getTitle());
    }

    @Test
    void publishQuest_Success() {
        Quest quest = new Quest();
        quest.setPublished(false);
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Quest result = questService.publishQuest(1L);
        assertTrue(result.isPublished());
    }
}