package org.virtualquest.platform.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.QuestCreateDTO;
import org.virtualquest.platform.dto.QuestDTO;
import org.virtualquest.platform.dto.QuestStatsDTO;
import org.virtualquest.platform.exception.AccessDeniedException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.CategoryRepository;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class QuestServiceTest {

    @Mock
    private QuestRepository questRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private QuestService questService;

    @Test
    void createDraft_Success() {
        Users creator = new Users();
        creator.setId(1L);

        QuestCreateDTO dto = new QuestCreateDTO();
        dto.setTitle("Title");
        dto.setDescription("Desc");
        dto.setDifficulty(Difficulty.EASY);
        dto.setCategoryIds(List.of(100L, 101L));

        Category cat1 = new Category();
        cat1.setId(100L);
        Category cat2 = new Category();
        cat2.setId(101L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(categoryRepository.findById(100L)).thenReturn(Optional.of(cat1));
        when(categoryRepository.findById(101L)).thenReturn(Optional.of(cat2));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Quest quest = questService.createDraft(1L, dto);

        assertEquals("Title", quest.getTitle());
        assertEquals("Desc", quest.getDescription());
        assertEquals(Difficulty.EASY, quest.getDifficulty());
        assertFalse(quest.isPublished());
        assertEquals(creator, quest.getCreator());
        assertTrue(quest.getCategories().contains(cat1));
        assertTrue(quest.getCategories().contains(cat2));
    }

    @Test
    void createDraft_UserNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        QuestCreateDTO dto = new QuestCreateDTO();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> questService.createDraft(1L, dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void createDraft_CategoryNotFound_Throws() {
        Users creator = new Users();
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        QuestCreateDTO dto = new QuestCreateDTO();
        dto.setCategoryIds(List.of(1L));

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> questService.createDraft(1L, dto));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void publishQuest_Success() {
        Quest quest = new Quest();
        quest.setPublished(false);
        when(questRepository.findById(10L)).thenReturn(Optional.of(quest));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Quest result = questService.publishQuest(10L);

        assertTrue(result.isPublished());
        verify(questRepository).save(quest);
    }

    @Test
    void publishQuest_QuestNotFound_Throws() {
        when(questRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> questService.publishQuest(10L));
        assertEquals("Quest not found", ex.getMessage());
    }

    @Test
    void getQuestById_Success() {
        Quest quest = new Quest();
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));

        Quest result = questService.getQuestById(1L);
        assertEquals(quest, result);
    }

    @Test
    void getQuestById_NotFound_Throws() {
        when(questRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questService.getQuestById(1L));
    }

    @Test
    void updateQuest_Success() {
        Quest quest = new Quest();
        quest.setTitle("Old");
        quest.setDescription("Old desc");
        quest.setDifficulty(Difficulty.EASY);
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        QuestDTO dto = new QuestDTO();
        dto.setTitle("New");
        dto.setDescription("New desc");
        dto.setDifficulty(Difficulty.MEDIUM);

        Quest updated = questService.updateQuest(1L, dto);

        assertEquals("New", updated.getTitle());
        assertEquals("New desc", updated.getDescription());
        assertEquals(Difficulty.MEDIUM, updated.getDifficulty());
    }

    @Test
    void updateQuest_NotFound_Throws() {
        when(questRepository.findById(1L)).thenReturn(Optional.empty());
        QuestDTO dto = new QuestDTO();

        assertThrows(ResourceNotFoundException.class,
                () -> questService.updateQuest(1L, dto));
    }

    @Test
    void addCategory_Success() {
        Quest quest = new Quest();
        Category category = new Category();

        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(questRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Quest result = questService.addCategory(1L, 2L);

        assertTrue(result.getCategories().contains(category));
    }

    @Test
    void addCategory_QuestNotFound_Throws() {
        lenient().when(questRepository.findById(1L)).thenReturn(Optional.empty());
        lenient().when(categoryRepository.findById(2L)).thenReturn(Optional.of(new Category()));

        assertThrows(ResourceNotFoundException.class,
                () -> questService.addCategory(1L, 2L));
    }

    @Test
    void addCategory_CategoryNotFound_Throws() {
        when(questRepository.findById(1L)).thenReturn(Optional.of(new Quest()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questService.addCategory(1L, 2L));
    }

    @Test
    void deleteQuest_AsAdmin_Success() {
        Quest quest = new Quest();
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));

        questService.deleteQuest(1L, 999L, "ROLE_ADMIN");

        verify(questRepository).delete(quest);
    }

    @Test
    void deleteQuest_AsModerator_Success() {
        Quest quest = new Quest();
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));

        questService.deleteQuest(1L, 999L, "ROLE_MODERATOR");

        verify(questRepository).delete(quest);
    }

    @Test
    void deleteQuest_AsCreator_Success() {
        Quest quest = new Quest();
        Users creator = new Users();
        creator.setId(123L);
        quest.setCreator(creator);

        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));

        questService.deleteQuest(1L, 123L, "ROLE_USER");

        verify(questRepository).delete(quest);
    }

    @Test
    void deleteQuest_OtherUser_Throws() {
        Quest quest = new Quest();
        Users creator = new Users();
        creator.setId(123L);
        quest.setCreator(creator);

        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> questService.deleteQuest(1L, 999L, "ROLE_USER"));
        assertEquals("Only quest creator", ex.getMessage());
    }

    @Test
    void deleteQuest_QuestNotFound_Throws() {
        when(questRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questService.deleteQuest(1L, 999L, "ROLE_ADMIN"));
    }

    @Test
    void findQuests_CallsRepository() {
        List<Quest> quests = List.of(new Quest(), new Quest());
        when(questRepository.findByFilters(true, Difficulty.EASY, List.of(1L, 2L))).thenReturn(quests);

        List<Quest> result = questService.findQuests(true, Difficulty.EASY, List.of(1L, 2L));
        assertEquals(2, result.size());
    }

    @Test
    void incrementStartedCount_Success() {
        when(questRepository.incrementStartedCount(1L)).thenReturn(1);

        assertDoesNotThrow(() -> questService.incrementStartedCount(1L));
    }

    @Test
    void incrementStartedCount_QuestNotFound_Throws() {
        when(questRepository.incrementStartedCount(1L)).thenReturn(0);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> questService.incrementStartedCount(1L));
        assertEquals("Quest not found", ex.getMessage());
    }

    @Test
    void getQuestStats_Success() {
        Quest quest = new Quest();
        quest.setStartedCount(5);
        quest.setCompletedCount(3);
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));

        QuestStatsDTO stats = questService.getQuestStats(1L);
        assertEquals(5, stats.started());
        assertEquals(3, stats.completed());
    }

    @Test
    void getQuestStats_QuestNotFound_Throws() {
        when(questRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> questService.getQuestStats(1L));
    }
}
