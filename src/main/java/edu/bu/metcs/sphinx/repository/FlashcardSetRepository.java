package edu.bu.metcs.sphinx.repository;

import edu.bu.metcs.sphinx.model.FlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, UUID> {

    List<FlashcardSet> findByOwnerId(UUID ownerId);

}
