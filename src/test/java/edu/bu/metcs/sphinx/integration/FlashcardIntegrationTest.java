package edu.bu.metcs.sphinx.integration;

import edu.bu.metcs.sphinx.BaseIntegrationTest;
import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.repository.FlashcardRepository;
import edu.bu.metcs.sphinx.repository.FlashcardSetRepository;
import edu.bu.metcs.sphinx.service.FlashcardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FlashcardIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    private FlashcardSetDTO testSetDTO;
    private FlashcardDTO testCardDTO;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();

        // Create test DTOs for our tests
        testSetDTO = new FlashcardSetDTO();
        testSetDTO.setName("Software Quality Management");
        testSetDTO.setDescription("Core concepts of SQM course");

        testCardDTO = new FlashcardDTO();
        testCardDTO.setQuestion("What is Test-Driven Development?");
        testCardDTO.setAnswer("A software development approach where tests are written before code");
        testCardDTO.setCreateReverse(true);
    }

    @Test
    void shouldCreateAndRetrieveFlashcardSet() {
        // When we create a new flashcard set
        FlashcardSet createdSet = flashcardService.createFlashcardSet(testSetDTO);

        // Then the set should be saved with an ID
        assertNotNull(createdSet.getId(), "Created set should have an ID");
        assertEquals(testSetDTO.getName(), createdSet.getName(), "Set name should match");
        assertEquals(testSetDTO.getDescription(), createdSet.getDescription(), "Set description should match");

        // And we should be able to retrieve it from the database
        FlashcardSet retrievedSet = flashcardService.getFlashcardSet(createdSet.getId());
        assertNotNull(retrievedSet, "Should be able to retrieve the created set");
        assertEquals(createdSet.getId(), retrievedSet.getId(), "Retrieved set should have the same ID");
    }

    @Test
    void shouldCreateFlashcardWithReverseCard() {
        // Given a flashcard set exists
        FlashcardSet createdSet = flashcardService.createFlashcardSet(testSetDTO);
        testCardDTO.setFlashcardSetId(createdSet.getId());

        // When we create a flashcard with createReverse=true
        Flashcard createdCard = flashcardService.createFlashcard(testCardDTO);

        // Then both the original and reverse cards should exist
        List<Flashcard> cardsInSet = flashcardService.getFlashcardsBySetId(createdSet.getId());

        // We should have two cards (original + reverse)
        assertEquals(2, cardsInSet.size(), "Should have created both original and reverse cards");

        // Verify we have both the original and reversed versions
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
        FlashcardSet createdSet = flashcardService.createFlashcardSet(testSetDTO);
        testCardDTO.setFlashcardSetId(createdSet.getId());
        Flashcard createdCard = flashcardService.createFlashcard(testCardDTO);

        // When we update the flashcard
        String updatedQuestion = "What is TDD?";
        String updatedAnswer = "A development approach emphasizing test-first methodology";

        FlashcardDTO updateDTO = new FlashcardDTO();
        updateDTO.setQuestion(updatedQuestion);
        updateDTO.setAnswer(updatedAnswer);

        Flashcard updatedCard = flashcardService.updateFlashcard(createdCard.getId(), updateDTO);

        // Then the changes should be persisted
        assertNotNull(updatedCard, "Updated card should not be null");
        assertEquals(updatedQuestion, updatedCard.getQuestion(), "Question should be updated");
        assertEquals(updatedAnswer, updatedCard.getAnswer(), "Answer should be updated");

        // And we should be able to retrieve the updated version
        Flashcard retrievedCard = flashcardService.getFlashcard(createdCard.getId());
        assertEquals(updatedQuestion, retrievedCard.getQuestion(), "Retrieved card should have updated question");
        assertEquals(updatedAnswer, retrievedCard.getAnswer(), "Retrieved card should have updated answer");
    }

    @Test
    void shouldDeleteFlashcardSet() {
        // Given a flashcard set with cards exists
        FlashcardSet createdSet = flashcardService.createFlashcardSet(testSetDTO);
        testCardDTO.setFlashcardSetId(createdSet.getId());
        flashcardService.createFlashcard(testCardDTO);

        // When we delete the set
        flashcardService.deleteFlashcardSet(createdSet.getId());

        // Then the set and all its cards should be deleted
        List<FlashcardSet> allSets = flashcardService.getAllFlashcardSets();
        assertTrue(allSets.isEmpty(), "No sets should remain after deletion");

        List<Flashcard> allCards = flashcardRepository.findAll();
        assertTrue(allCards.isEmpty(), "No cards should remain after set deletion");
    }
}