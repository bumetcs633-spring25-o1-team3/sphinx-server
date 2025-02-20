package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.dto.FlashcardDTO;
import edu.bu.metcs.sphinx.model.Flashcard;
import edu.bu.metcs.sphinx.model.FlashcardSet;
import edu.bu.metcs.sphinx.security.util.SecurityUtils;
import edu.bu.metcs.sphinx.service.FlashcardService;
import edu.bu.metcs.sphinx.service.FlashcardSetService;
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
    private final FlashcardSetService flashcardSetService;

    @Autowired
    public FlashcardController(FlashcardService flashcardService, FlashcardSetService flashcardSetService) {
        this.flashcardService = flashcardService;
        this.flashcardSetService = flashcardSetService;
    }

    @PostMapping
    public ResponseEntity<FlashcardDTO> createCard(@RequestBody FlashcardDTO flashcardDTO) {
        try {
            // Verify the user owns the flashcard set
            FlashcardSet flashcardSet = flashcardSetService.getFlashcardSet(flashcardDTO.getFlashcardSetId());
            try {
                SecurityUtils.verifyCurrentUser(flashcardSet.getOwner().getId());
            } catch (RuntimeException e) {
                if (e.getMessage().equals("User not authenticated")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                } else if (e.getMessage().equals("Unauthorized access")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                throw e;
            }

            Flashcard createdCard = flashcardService.createFlashcard(flashcardDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(FlashcardDTO.fromEntity(createdCard));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FlashcardSet not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/set/{setId}")
    public ResponseEntity<List<FlashcardDTO>> getCardsInSet(@PathVariable UUID setId) {
        try {

            FlashcardSet flashcardSet = flashcardSetService.getFlashcardSet(setId);
            // If the set is not public, verify ownership
            if (!flashcardSet.isPublic()) {
                try {
                    SecurityUtils.verifyCurrentUser(flashcardSet.getOwner().getId());
                } catch (RuntimeException e) {
                    if (e.getMessage().equals("User not authenticated") ||
                            e.getMessage().equals("Unauthorized access")) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                    throw e;
                }
            }
            List<Flashcard> cards = flashcardService.getFlashcardsBySetId(setId);
            List<FlashcardDTO> cardDTOs = cards.stream()
                    .map(FlashcardDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(cardDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FlashcardSet not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardDTO> getCard(@PathVariable UUID id) {
        try {
            Flashcard card = flashcardService.getFlashcard(id);
            // If the set is not public, verify ownership
            FlashcardSet parentSet = card.getFlashcardSet();
            if (!parentSet.isPublic()) {
                try {
                    SecurityUtils.verifyCurrentUser(parentSet.getOwner().getId());
                } catch (RuntimeException e) {
                    if (e.getMessage().equals("User not authenticated") ||
                            e.getMessage().equals("Unauthorized access")) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                    throw e;
                }
            }

            return ResponseEntity.ok(FlashcardDTO.fromEntity(card));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Flashcard not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/set/{setId}")
    public ResponseEntity<FlashcardDTO> updateCard(
            @PathVariable UUID id,
            @PathVariable UUID setId,
            @RequestBody FlashcardDTO flashcardDTO) {
        try {
            // Verify the set exists and user owns it
            FlashcardSet flashcardSet = flashcardSetService.getFlashcardSet(setId);
            try {
                SecurityUtils.verifyCurrentUser(flashcardSet.getOwner().getId());
            } catch (RuntimeException e) {
                if (e.getMessage().equals("User not authenticated")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                } else if (e.getMessage().equals("Unauthorized access")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                throw e;
            }

            Flashcard updatedCard = flashcardService.updateFlashcard(id, setId, flashcardDTO);
            return ResponseEntity.ok(FlashcardDTO.fromEntity(updatedCard));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") ||
                    e.getMessage().equals("Flashcard does not belong to specified set")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/set/{setId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable UUID id,
            @PathVariable UUID setId) {
        try {
            // Verify the set exists and user owns it
            FlashcardSet flashcardSet = flashcardSetService.getFlashcardSet(setId);
            try {
                SecurityUtils.verifyCurrentUser(flashcardSet.getOwner().getId());
            } catch (RuntimeException e) {
                if (e.getMessage().equals("User not authenticated")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                } else if (e.getMessage().equals("Unauthorized access")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                throw e;
            }

            flashcardService.deleteFlashcard(id, setId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}