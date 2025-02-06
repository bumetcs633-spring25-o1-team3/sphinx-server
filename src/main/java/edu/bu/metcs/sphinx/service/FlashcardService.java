package edu.bu.metcs.sphinx.service;

import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.repository.FlashcardRepository;
import edu.bu.metcs.sphinx.repository.FlashcardSetRepository;
import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final FlashcardSetRepository flashcardSetRepository;

    @Autowired
    public FlashcardService(FlashcardRepository flashcardRepository,
                            FlashcardSetRepository flashcardSetRepository) {
        this.flashcardRepository = flashcardRepository;
        this.flashcardSetRepository = flashcardSetRepository;
    }

    public FlashcardSet createFlashcardSet(FlashcardSetDTO dto) {
        FlashcardSet flashcardSet = new FlashcardSet();
        flashcardSet.setName(dto.getName());
        flashcardSet.setDescription(dto.getDescription());
        return flashcardSetRepository.save(flashcardSet);
    }

    public List<FlashcardSet> getAllFlashcardSets() {
        return flashcardSetRepository.findAll();
    }

    public FlashcardSet getFlashcardSet(UUID id) {
        return flashcardSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));
    }

    public FlashcardSet updateFlashcardSet(UUID id, FlashcardSetDTO dto) {
        FlashcardSet existingSet = flashcardSetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));

        existingSet.setName(dto.getName());
        existingSet.setDescription(dto.getDescription());
        return flashcardSetRepository.save(existingSet);
    }

    public void deleteFlashcardSet(UUID id) {
        flashcardSetRepository.deleteById(id);
    }

    public Flashcard createFlashcard(FlashcardDTO dto) {
        FlashcardSet flashcardSet = flashcardSetRepository.findById(dto.getFlashcardSetId())
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));

        // Create the original flashcard
        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(dto.getQuestion());
        flashcard.setAnswer(dto.getAnswer());
        flashcard.setFlashcardSet(flashcardSet);

        Flashcard savedCard = flashcardRepository.save(flashcard);

        // If createReverse is true, create and save the reverse card
        if (dto.isCreateReverse()) {
            Flashcard reverseCard = new Flashcard();
            reverseCard.setQuestion(dto.getAnswer());    // Swap question and answer
            reverseCard.setAnswer(dto.getQuestion());
            reverseCard.setFlashcardSet(flashcardSet);
            flashcardRepository.save(reverseCard);
        }

        return savedCard;
    }

    public Flashcard getFlashcard(UUID id) {
        return flashcardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));
    }

    public List<Flashcard> getFlashcardsBySetId(UUID setId) {
        if (!flashcardSetRepository.existsById(setId)) {
            throw new RuntimeException("FlashcardSet not found");
        }
        return flashcardRepository.findByFlashcardSetId(setId);
    }

    public Flashcard updateFlashcard(UUID id, FlashcardDTO dto) {
        Flashcard existingCard = flashcardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));

        existingCard.setQuestion(dto.getQuestion());
        existingCard.setAnswer(dto.getAnswer());
        return flashcardRepository.save(existingCard);
    }

    public void deleteFlashcard(UUID id) {
        flashcardRepository.deleteById(id);
    }
}