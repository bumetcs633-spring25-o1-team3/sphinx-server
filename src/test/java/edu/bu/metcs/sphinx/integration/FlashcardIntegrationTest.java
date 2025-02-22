package edu.bu.metcs.sphinx.integration;

import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.model.User;
import edu.bu.metcs.sphinx.repository.FlashcardRepository;
import edu.bu.metcs.sphinx.repository.FlashcardSetRepository;
import edu.bu.metcs.sphinx.repository.UserRepository;
import edu.bu.metcs.sphinx.service.FlashcardSetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FlashcardIntegrationTest {

    @Autowired
    private FlashcardSetService flashcardSetService;

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

        testSetDTO = new FlashcardSetDTO();
        testSetDTO.setName("Software Quality Management");
        testSetDTO.setDescription("Core concepts of SQM course");
        testSetDTO.setPublic(true);
        testSetDTO.setOwnerId(testUser.getId());

        testCardDTO = new FlashcardDTO();
        testCardDTO.setQuestion("What is Test-Driven Development?");
        testCardDTO.setAnswer("A software development approach where tests are written before code");
        testCardDTO.setCreateReverse(true);

        Set<FlashcardDTO> flashcards = new HashSet<>();
        flashcards.add(testCardDTO);
        testSetDTO.setFlashcards(flashcards);
    }

    @Test
    void shouldCreateFlashcardSetWithCards() {
        // When we create a new flashcard set
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(testSetDTO);

        // Then the set should be saved with correct properties
        assertNotNull(createdSet.getId(), "Created set should have an ID");
        assertEquals(testSetDTO.getName(), createdSet.getName(), "Set name should match");
        assertEquals(testSetDTO.getDescription(), createdSet.getDescription(), "Set description should match");
        assertEquals(testUser.getId(), createdSet.getOwner().getId(), "Owner should match");

        // And the flashcards should be created (including reverse card)
        assertEquals(2, createdSet.getFlashcards().size(), "Should have two cards (original + reverse)");
        for (Flashcard card : createdSet.getFlashcards()) {
            if (card.getQuestion().equals(testCardDTO.getQuestion())) {
                assertEquals(testCardDTO.getAnswer(), card.getAnswer());
            } else if (card.getQuestion().equals(testCardDTO.getAnswer())) {
                assertEquals(testCardDTO.getQuestion(), card.getAnswer());
            }
        }
    }

    @Test
    void shouldUpdateFlashcardSetWithCards() {
        // Given a created flashcard set
        FlashcardSet originalSet = flashcardSetService.createFlashcardSet(testSetDTO);

        // When we update the set
        FlashcardSetDTO updateDTO = new FlashcardSetDTO();
        updateDTO.setName("Updated SQM");
        updateDTO.setDescription("Updated description");
        updateDTO.setPublic(false);

        // Modify existing card and add a new one
        Set<FlashcardDTO> updatedCards = new HashSet<>();

        // Modified version of existing card
        FlashcardDTO modifiedCard = new FlashcardDTO();
        modifiedCard.setId(originalSet.getFlashcards().iterator().next().getId());
        modifiedCard.setQuestion("Updated TDD Question");
        modifiedCard.setAnswer("Updated TDD Answer");
        updatedCards.add(modifiedCard);

        // New card
        FlashcardDTO newCard = new FlashcardDTO();
        newCard.setQuestion("What is CI/CD?");
        newCard.setAnswer("Continuous Integration/Continuous Deployment");
        newCard.setCreateReverse(true);
        updatedCards.add(newCard);

        updateDTO.setFlashcards(updatedCards);

        FlashcardSet updatedSet = flashcardSetService.updateFlashcardSet(originalSet.getId(), updateDTO);

        // Then the set should be updated
        assertEquals("Updated SQM", updatedSet.getName());
        assertEquals("Updated description", updatedSet.getDescription());
        assertFalse(updatedSet.isPublic());


        assertEquals(3, updatedSet.getFlashcards().size());
        for (Flashcard card : updatedSet.getFlashcards()) {
            switch (card.getQuestion()) {
                case "Updated TDD Question" -> assertEquals("Updated TDD Answer", card.getAnswer());
                case "What is CI/CD?" -> assertEquals("Continuous Integration/Continuous Deployment", card.getAnswer());
                case "Continuous Integration/Continuous Deployment" -> assertEquals("What is CI/CD?", card.getAnswer());
            }
        }
    }

    @Test
    void shouldGetFlashcardSetsByOwner() {
        // Given multiple flashcard sets for different users
        FlashcardSet set1 = flashcardSetService.createFlashcardSet(testSetDTO);

        User anotherUser = new User();
        anotherUser.setEmail("another@bu.edu");
        anotherUser.setName("Another User");
        userRepository.save(anotherUser);

        FlashcardSetDTO anotherSetDTO = new FlashcardSetDTO();
        anotherSetDTO.setName("Another Set");
        anotherSetDTO.setDescription("Another description");
        anotherSetDTO.setOwnerId(anotherUser.getId());
        FlashcardSet set2 = flashcardSetService.createFlashcardSet(anotherSetDTO);

        // When we get sets by owner
        List<FlashcardSet> userSets = flashcardSetService.getFlashcardSetsByOwnerId(testUser.getId());

        // Then we should only get the sets owned by the test user
        assertEquals(1, userSets.size());
        assertEquals(set1.getId(), userSets.get(0).getId());
    }

    @Test
    void shouldDeleteFlashcardSet() {
        // Given a created flashcard set
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(testSetDTO);
        UUID setId = createdSet.getId();

        // When we delete the set
        flashcardSetService.deleteFlashcardSet(setId);

        // Then the set and its cards should be deleted
        assertFalse(flashcardSetRepository.existsById(setId));
        assertEquals(0, flashcardRepository.findByFlashcardSetId(setId).size());
    }
}