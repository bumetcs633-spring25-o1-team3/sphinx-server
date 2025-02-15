package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flashcard")
public class FlashcardController {

    private final FlashcardService flashcardService;

    @Autowired
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @PostMapping
    public ResponseEntity<FlashcardSet> createCard(@RequestBody FlashcardDTO flashcardDTO) {
        try {
            FlashcardSet updatedSet = flashcardService.createFlashcard(flashcardDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedSet);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/set/{setId}")
    public ResponseEntity<List<FlashcardDTO>> getCardsInSet(@PathVariable UUID setId) {
        try {
            List<Flashcard> cards = flashcardService.getFlashcardsBySetId(setId);
            List<FlashcardDTO> dtos = cards.stream()
                    .map(FlashcardDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardDTO> getCard(@PathVariable UUID id) {
        try {
            Flashcard card = flashcardService.getFlashcard(id);
            return ResponseEntity.ok(FlashcardDTO.fromEntity(card));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/set/{setId}")
    public ResponseEntity<FlashcardDTO> updateCard(
            @PathVariable UUID id,
            @PathVariable UUID setId,
            @RequestBody FlashcardDTO flashcardDTO) {
        try {
            Flashcard updatedCard = flashcardService.updateFlashcard(id, setId, flashcardDTO);
            return ResponseEntity.ok(FlashcardDTO.fromEntity(updatedCard));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/set/{setId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable UUID id,
            @PathVariable UUID setId) {
        try {
            flashcardService.deleteFlashcard(id, setId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}