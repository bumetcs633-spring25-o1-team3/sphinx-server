package edu.bu.metcs.sphinx.dto;

import edu.bu.metcs.sphinx.model.FlashcardSet;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class FlashcardSetDTO {
    private UUID id;
    private String name;
    private String description;
    private Set<FlashcardDTO> flashcards = new HashSet<>();
    private String ownerName;
    private UUID ownerId;

    public FlashcardSetDTO() {}

    public FlashcardSetDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<FlashcardDTO> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(Set<FlashcardDTO> flashcards) {
        this.flashcards = flashcards;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public static FlashcardSetDTO fromEntity(FlashcardSet entity) {
        FlashcardSetDTO dto = new FlashcardSetDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        if (entity.getOwner() != null) {
            dto.setOwnerName(entity.getOwner().getName());
            dto.setOwnerId(entity.getOwner().getId());
        }
        return dto;
    }
}
