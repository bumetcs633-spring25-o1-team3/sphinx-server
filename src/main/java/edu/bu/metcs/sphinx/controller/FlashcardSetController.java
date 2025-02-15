package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.dto.FlashcardSetDTO;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.security.util.SecurityUtils;
import edu.bu.metcs.sphinx.service.FlashcardSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flashcard-set")
public class FlashcardSetController {

    private final FlashcardSetService flashcardSetService;

    @Autowired
    public FlashcardSetController(FlashcardSetService flashcardSetService) {
        this.flashcardSetService = flashcardSetService;
    }

    @PostMapping
    public ResponseEntity<FlashcardSetDTO> createSet(@RequestBody FlashcardSetDTO flashcardSetDTO) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        flashcardSetDTO.setOwnerId(currentUserId);
        FlashcardSet createdSet = flashcardSetService.createFlashcardSet(flashcardSetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(FlashcardSetDTO.fromEntity(createdSet));
    }

    @GetMapping
    public ResponseEntity<List<FlashcardSetDTO>> getAllSets() {
        List<FlashcardSet> sets = flashcardSetService.getAllFlashcardSets();
        List<FlashcardSetDTO> dtos = sets.stream()
                .map(FlashcardSetDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardSetDTO> getSet(@PathVariable UUID id) {
        try {
            FlashcardSet set = flashcardSetService.getFlashcardSet(id);
            return ResponseEntity.ok(FlashcardSetDTO.fromEntity(set));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashcardSetDTO> updateSet(
            @PathVariable UUID id,
            @RequestBody FlashcardSetDTO flashcardSetDTO) {
        try {
            FlashcardSet updatedSet = flashcardSetService.updateFlashcardSet(id, flashcardSetDTO);
            return ResponseEntity.ok(FlashcardSetDTO.fromEntity(updatedSet));
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