package org.virtualquest.platform.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class QuestRepositoryTest {

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByDifficulty returns correct quests")
    void testFindByDifficulty() {
        Quest easyQuest = new Quest();
        easyQuest.setDifficulty(Difficulty.EASY);
        questRepository.save(easyQuest);

        List<Quest> results = questRepository.findByDifficulty(Difficulty.EASY);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDifficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    @DisplayName("findByCreatorId returns correct quests")
    void testFindByCreatorId() {
        Users creator = new Users();
        creator.setEmail("test" + UUID.randomUUID() + "@example.com");
        creator.setUsername("testuser");
        creator.setPassword("password");
        creator.setFullName("Test User");
        userRepository.save(creator);

        Quest quest = new Quest();
        quest.setCreator(creator);
        questRepository.save(quest);

        List<Quest> results = questRepository.findByCreatorId(creator.getId());
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCreator().getId()).isEqualTo(creator.getId());
    }

    @Test
    void findByFilters_FiltersWork() {
        Users user = new Users();
        user.setEmail("test" + UUID.randomUUID() + "@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFullName("Test User");
        user = userRepository.save(user);

        Category cat1 = new Category();
        cat1.setName("Category 1");
        cat1 = categoryRepository.save(cat1);

        Quest quest1 = new Quest();
        quest1.setTitle("Quest1");
        quest1.setDifficulty(Difficulty.EASY);
        quest1.setPublished(true);
        quest1.setCreator(user);
        quest1.getCategories().add(cat1);
        questRepository.save(quest1);

        List<Quest> quests = questRepository.findByFilters(true, Difficulty.EASY, List.of(cat1.getId()));

        assertThat(quests).isNotEmpty();
        assertThat(quests.get(0).getTitle()).isEqualTo("Quest1");
    }
}
