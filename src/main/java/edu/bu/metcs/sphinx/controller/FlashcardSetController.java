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
        try {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            flashcardSetDTO.setOwnerId(currentUserId);
            FlashcardSet createdSet = flashcardSetService.createFlashcardSet(flashcardSetDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(FlashcardSetDTO.fromEntity(createdSet));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FlashcardSetDTO>> getAllSets() {
        List<FlashcardSet> sets = flashcardSetService.getAllFlashcardSets();
        List<FlashcardSetDTO> dtos = sets.stream()
                .map(FlashcardSetDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-sets")
    public ResponseEntity<List<FlashcardSetDTO>> getMyFlashcardSets() {
        try {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            List<FlashcardSet> userSets = flashcardSetService.getFlashcardSetsByOwnerId(currentUserId);
            List<FlashcardSetDTO> setDTOs = userSets.stream()
                    .map(FlashcardSetDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(setDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
            FlashcardSet existingSet = flashcardSetService.getFlashcardSet(id);
            // Verify current user is the owner
            try {
                SecurityUtils.verifyCurrentUser(existingSet.getOwner().getId());
            } catch (RuntimeException e) {
                if (e.getMessage().equals("User not authenticated")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                } else if (e.getMessage().equals("Unauthorized access")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                throw e;
            }
            FlashcardSet updatedSet = flashcardSetService.updateFlashcardSet(id, flashcardSetDTO);
            return ResponseEntity.ok(FlashcardSetDTO.fromEntity(updatedSet));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FlashcardSet not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable UUID id) {
        try {
            FlashcardSet existingSet = flashcardSetService.getFlashcardSet(id);
            // Verify current user is the owner
            try {
                SecurityUtils.verifyCurrentUser(existingSet.getOwner().getId());
            } catch (RuntimeException e) {
                if (e.getMessage().equals("User not authenticated")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                } else if (e.getMessage().equals("Unauthorized access")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                throw e;
            }
            flashcardSetService.deleteFlashcardSet(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("FlashcardSet not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}