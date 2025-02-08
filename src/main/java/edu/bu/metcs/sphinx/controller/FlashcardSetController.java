package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.service.FlashcardSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/flashcard-set")
public class FlashcardSetController {

    private final FlashcardSetService flashcardSetService;

    @Autowired
    public FlashcardSetController(FlashcardSetService flashcardSetService) {
        this.flashcardSetService = flashcardSetService;
    }

    @PostMapping
    public ResponseEntity<FlashcardSet> createSet(@RequestBody FlashcardSetDTO flashcardSetDTO) {
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(flashcardSetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSet);
    }

    @GetMapping
    public ResponseEntity<List<FlashcardSet>> getAllSets() {
        List<FlashcardSet> sets = flashcardSetService.getAllFlashcardSets();
        return ResponseEntity.ok(sets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardSet> getSet(@PathVariable UUID id) {
        try {
            FlashcardSet set = flashcardSetService.getFlashcardSet(id);
            return ResponseEntity.ok(set);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashcardSet> updateSet(
            @PathVariable UUID id,
            @RequestBody FlashcardSetDTO flashcardSetDTO) {
        try {
            FlashcardSet updatedSet = flashcardSetService.updateFlashcardSet(id, flashcardSetDTO);
            return ResponseEntity.ok(updatedSet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable UUID id) {
        try {
            flashcardSetService.deleteFlashcardSet(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}