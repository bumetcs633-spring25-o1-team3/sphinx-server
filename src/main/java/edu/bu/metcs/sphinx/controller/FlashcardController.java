package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class FlashcardController {

    private final FlashcardService flashcardService;

    @Autowired
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @PostMapping("/flashcard-set")
    public ResponseEntity<FlashcardSet> createSet(@RequestBody FlashcardSetDTO flashcardSetDTO) {
        FlashcardSet createdSet = flashcardService.createFlashcardSet(flashcardSetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSet);
    }

    @GetMapping("/flashcard-set")
    public ResponseEntity<List<FlashcardSet>> getAllSets() {
        List<FlashcardSet> sets = flashcardService.getAllFlashcardSets();
        return ResponseEntity.ok(sets);
    }

    @GetMapping("/flashcard-set/{id}")
    public ResponseEntity<FlashcardSet> getSet(@PathVariable UUID id) {
        try {
            FlashcardSet set = flashcardService.getFlashcardSet(id);
            return ResponseEntity.ok(set);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/flashcard-set/{id}")
    public ResponseEntity<FlashcardSet> updateSet(
            @PathVariable UUID id,
            @RequestBody FlashcardSetDTO flashcardSetDTO) {
        try {
            FlashcardSet updatedSet = flashcardService.updateFlashcardSet(id, flashcardSetDTO);
            return ResponseEntity.ok(updatedSet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/flashcard-set/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable UUID id) {
        try {
            flashcardService.deleteFlashcardSet(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/flashcard")
    public ResponseEntity<Flashcard> createCard(@RequestBody FlashcardDTO flashcardDTO) {
        try {
            Flashcard createdCard = flashcardService.createFlashcard(flashcardDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/flashcard/set/{setId}")
    public ResponseEntity<List<Flashcard>> getCardsInSet(@PathVariable UUID setId) {
        try {
            List<Flashcard> cards = flashcardService.getFlashcardsBySetId(setId);
            return ResponseEntity.ok(cards);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/flashcard/{id}")
    public ResponseEntity<Flashcard> getCard(@PathVariable UUID id) {
        try {
            Flashcard card = flashcardService.getFlashcard(id);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/flashcard/{id}")
    public ResponseEntity<Flashcard> updateCard(
            @PathVariable UUID id,
            @RequestBody FlashcardDTO flashcardDTO) {
        try {
            Flashcard updatedCard = flashcardService.updateFlashcard(id, flashcardDTO);
            return ResponseEntity.ok(updatedCard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/flashcard/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        try {
            flashcardService.deleteFlashcard(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}