package edu.bu.metcs.sphinx.repository;

import edu.bu.metcs.sphinx.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {

    List<Flashcard> findByFlashcardSetId(UUID flashcardSetId);

}
