package edu.bu.metcs.sphinx.service;

import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.repository.FlashcardRepository;
import edu.bu.metcs.sphinx.repository.FlashcardSetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FlashcardSetService {

    private final FlashcardSetRepository flashcardSetRepository;

    @Autowired
    public FlashcardSetService(FlashcardSetRepository flashcardSetRepository) {
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
}
