package edu.bu.metcs.sphinx.service;

import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.model.User;
import edu.bu.metcs.sphinx.repository.FlashcardSetRepository;
import edu.bu.metcs.sphinx.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlashcardSetService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final UserRepository userRepository;

    @Autowired
    public FlashcardSetService(FlashcardSetRepository flashcardSetRepository,
                               UserRepository userRepository) {
        this.flashcardSetRepository = flashcardSetRepository;
        this.userRepository = userRepository;
    }

    public FlashcardSet createFlashcardSet(FlashcardSetDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setName(dto.getName());
        flashcardSet.setDescription(dto.getDescription());
        flashcardSet.setPublic(dto.isPublic());
        flashcardSet.setOwner(owner);

        if (dto.getFlashcards() != null) {
            for (FlashcardDTO cardDto : dto.getFlashcards()) {
                Flashcard flashcard = new Flashcard();
                flashcard.setQuestion(cardDto.getQuestion());
                flashcard.setAnswer(cardDto.getAnswer());
                flashcardSet.addFlashcard(flashcard);

                // create a reverse card
                if (cardDto.isCreateReverse()) {
                    Flashcard reverseCard = new Flashcard();
                    reverseCard.setQuestion(cardDto.getAnswer());
                    reverseCard.setAnswer(cardDto.getQuestion());
                    flashcardSet.addFlashcard(reverseCard);
                }
            }
        }

        return flashcardSetRepository.save(flashcardSet);
    }

    public List<FlashcardSet> getFlashcardSetsByOwnerId(UUID userId) {
        return flashcardSetRepository.findByOwnerId(userId);
    }

    public FlashcardSet getFlashcardSet(UUID id) {
        return flashcardSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));
    }

    @Transactional
    public FlashcardSet updateFlashcardSet(UUID setId, FlashcardSetDTO dto) {
        FlashcardSet existingSet = flashcardSetRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));

        existingSet.setName(dto.getName());
        existingSet.setDescription(dto.getDescription());
        existingSet.setPublic(dto.isPublic());

        // Create a map of existing flashcards by ID
        Map<UUID, Flashcard> existingCards = existingSet.getFlashcards().stream()
                .collect(Collectors.toMap(Flashcard::getId, card -> card));

        // Clear the existing flashcards but don't delete them yet
        existingSet.getFlashcards().clear();

        // Process the incoming flashcards
        Set<UUID> processedCards = new HashSet<>();
        for (FlashcardDTO cardDto : dto.getFlashcards()) {
            if (cardDto.getId() != null && existingCards.containsKey(cardDto.getId())) {
                // Update existing flashcard
                Flashcard existingCard = existingCards.get(cardDto.getId());
                existingCard.setQuestion(cardDto.getQuestion());
                existingCard.setAnswer(cardDto.getAnswer());
                existingSet.addFlashcard(existingCard);
                processedCards.add(cardDto.getId());
            } else {
                // Add new flashcard
                addFlashcardToSet(existingSet, cardDto);
            }
        }

        // Remove flashcards that weren't in the update
        existingCards.forEach((id, card) -> {
            if (!processedCards.contains(id)) {
                card.setFlashcardSet(null); // This will allow JPA to delete the card
            }
        });

        return flashcardSetRepository.save(existingSet);
    }

    private void addFlashcardToSet(FlashcardSet set, FlashcardDTO dto) {
        // Add main flashcard
        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(dto.getQuestion());
        flashcard.setAnswer(dto.getAnswer());
        set.addFlashcard(flashcard);

        // Add reverse card if requested
        if (dto.isCreateReverse()) {
            Flashcard reverseCard = new Flashcard();
            reverseCard.setQuestion(dto.getAnswer());
            reverseCard.setAnswer(dto.getQuestion());
            set.addFlashcard(reverseCard);
        }
    }

    public void deleteFlashcardSet(UUID id) {
        flashcardSetRepository.deleteById(id);
    }
}
