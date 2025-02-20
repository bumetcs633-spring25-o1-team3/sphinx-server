package edu.bu.metcs.sphinx.integration;

import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.model.User;
import edu.bu.metcs.sphinx.repository.FlashcardRepository;
import edu.bu.metcs.sphinx.repository.FlashcardSetRepository;
import edu.bu.metcs.sphinx.repository.UserRepository;
import edu.bu.metcs.sphinx.service.FlashcardService;
import edu.bu.metcs.sphinx.service.FlashcardSetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FlashcardIntegrationTest {

    @Autowired
    private FlashcardSetService flashcardSetService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private UserRepository userRepository;

    private FlashcardSetDTO testSetDTO;
    private FlashcardDTO testCardDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@bu.edu");
        testUser.setName("Test User");
        userRepository.save(testUser);

        // Create test DTOs for our tests
        testSetDTO = new FlashcardSetDTO();
        testSetDTO.setName("Software Quality Management");
        testSetDTO.setDescription("Core concepts of SQM course");
        testSetDTO.setPublic(true);
        testSetDTO.setOwnerId(testUser.getId());

        testCardDTO = new FlashcardDTO();
        testCardDTO.setQuestion("What is Test-Driven Development?");
        testCardDTO.setAnswer("A software development approach where tests are written before code");
        testCardDTO.setCreateReverse(true);
    }

    @Test
    void shouldCreateAndRetrieveFlashcardSet() {
        // When we create a new flashcard set
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(testSetDTO);

        // Then the set should be saved with an ID
        assertNotNull(createdSet.getId(), "Created set should have an ID");
        assertEquals(testSetDTO.getName(), createdSet.getName(), "Set name should match");
        assertEquals(testSetDTO.getDescription(), createdSet.getDescription(), "Set description should match");

        // And we should be able to retrieve it from the database
        FlashcardSet retrievedSet = flashcardSetService.getFlashcardSet(createdSet.getId());
        assertNotNull(retrievedSet, "Should be able to retrieve the created set");
        assertEquals(createdSet.getId(), retrievedSet.getId(), "Retrieved set should have the same ID");
    }

    @Test
    void shouldCreateFlashcardWithReverseCard() {
        // Given a flashcard set exists
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(testSetDTO);
        testCardDTO.setFlashcardSetId(createdSet.getId());

        // When we create a flashcard with createReverse=true
        flashcardService.createFlashcard(testCardDTO);

        // Then both the original and reverse cards should exist
        List<Flashcard> cardsInSet = flashcardService.getFlashcardsBySetId(createdSet.getId());
        assertEquals(2, cardsInSet.size(), "Should have created both original and reverse cards");
        boolean hasOriginal = cardsInSet.stream()
                .anyMatch(card -> card.getQuestion().equals(testCardDTO.getQuestion()));
        boolean hasReverse = cardsInSet.stream()
                .anyMatch(card -> card.getQuestion().equals(testCardDTO.getAnswer()));

        assertTrue(hasOriginal, "Should have the original card");
        assertTrue(hasReverse, "Should have the reverse card");
    }

    @Test
    void shouldUpdateFlashcard() {
        // Given a flashcard exists
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(testSetDTO);
        testCardDTO.setFlashcardSetId(createdSet.getId());
        flashcardService.createFlashcard(testCardDTO);

        Flashcard createdCard = flashcardRepository.findByFlashcardSetId(createdSet.getId()).get(0);

        // When we update the flashcard
        String updatedQuestion = "What is TDD?";
        String updatedAnswer = "A development approach emphasizing test-first methodology";

        FlashcardDTO updateDTO = new FlashcardDTO();
        updateDTO.setQuestion(updatedQuestion);
        updateDTO.setAnswer(updatedAnswer);

        Flashcard updatedCard = flashcardService.updateFlashcard(
                createdCard.getId(),
                createdSet.getId(),
                updateDTO);

        // Then the changes should be persisted
        assertNotNull(updatedCard, "Updated card should not be null");
        assertEquals(updatedQuestion, updatedCard.getQuestion(), "Question should be updated");
        assertEquals(updatedAnswer, updatedCard.getAnswer(), "Answer should be updated");

        Flashcard retrievedCard = flashcardService.getFlashcard(createdCard.getId());
        assertEquals(updatedQuestion, retrievedCard.getQuestion(), "Retrieved card should have updated question");
        assertEquals(updatedAnswer, retrievedCard.getAnswer(), "Retrieved card should have updated answer");
    }

    @Test
    void shouldDeleteFlashcardSet() {
        // Given a flashcard set with cards exists
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(testSetDTO);
        testCardDTO.setFlashcardSetId(createdSet.getId());
        flashcardService.createFlashcard(testCardDTO);

        // When we delete the set
        flashcardSetService.deleteFlashcardSet(createdSet.getId());

        // Then the set and all its cards should be deleted
        List<FlashcardSet> allSets = flashcardSetService.getAllFlashcardSets();
        assertTrue(allSets.isEmpty(), "No sets should remain after deletion");

        List<Flashcard> allCards = flashcardRepository.findAll();
        assertTrue(allCards.isEmpty(), "No cards should remain after set deletion");
        assertTrue(userRepository.existsById(testUser.getId()), "User should still exist after set deletion");
    }

    @Test
    void shouldUpdateFlashcardSetVisibility() {
        // Given a public flashcard set
        FlashcardSet publicSet = flashcardSetService.createFlashcardSet(testSetDTO);
        assertTrue(publicSet.isPublic(), "Set should start as public");

        // When updating to private
        testSetDTO.setPublic(false);
        FlashcardSet updatedSet = flashcardSetService.updateFlashcardSet(publicSet.getId(), testSetDTO);

        // Then the visibility should be updated
        assertFalse(updatedSet.isPublic(), "Set should now be private");
        FlashcardSet retrievedSet = flashcardSetService.getFlashcardSet(publicSet.getId());
        assertFalse(retrievedSet.isPublic(), "Retrieved set should reflect visibility change");
    }
}