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



    public FlashcardSet createFlashcard(FlashcardDTO dto) {
        FlashcardSet flashcardSet = flashcardSetRepository.findById(dto.getFlashcardSetId())
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));

        // Create the main flashcard
        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(dto.getQuestion());
        flashcard.setAnswer(dto.getAnswer());
        flashcardSet.addFlashcard(flashcard);

        flashcardSetRepository.save(flashcardSet);

        // If createReverse is true, create and save the reverse card
        if (dto.isCreateReverse()) {
            Flashcard reverseCard = new Flashcard();
            reverseCard.setQuestion(dto.getAnswer());    // Swap question and answer
            reverseCard.setAnswer(dto.getQuestion());
            flashcardSet.addFlashcard(reverseCard);
            flashcardSetRepository.save(flashcardSet);
        }

        return flashcardSet;
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

    public Flashcard updateFlashcard(UUID flashcardId, UUID flashcardSetId, FlashcardDTO dto) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));

        if(!flashcard.getFlashcardSet().getId().equals(flashcardSetId)) {
            throw new RuntimeException("Flashcard does not belong to specified set");
        }

        flashcard.setQuestion(dto.getQuestion());
        flashcard.setAnswer(dto.getAnswer());

        return flashcardRepository.save(flashcard);
    }

    public void deleteFlashcard(UUID flashcardId, UUID flashcardSetId) {
        FlashcardSet flashcardSet = flashcardSetRepository.findById(flashcardSetId)
                .orElseThrow(() -> new RuntimeException("FlashcardSet not found"));

        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));

        flashcardSet.removeFlashcard(flashcard);
        flashcardSetRepository.save(flashcardSet);
    }


}