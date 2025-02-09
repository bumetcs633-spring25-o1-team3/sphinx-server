package edu.bu.metcs.sphinx.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "flashcard_set")
public class FlashcardSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "flashcardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Flashcard> flashcards = new HashSet<>();

    public FlashcardSet() {}

    public FlashcardSet(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addFlashcard(Flashcard flashcard) {
        flashcards.add(flashcard);
        flashcard.setFlashcardSet(this);
    }

    public void removeFlashcard(Flashcard flashcard) {
        flashcards.remove(flashcard);
        flashcard.setFlashcardSet(null);
    }

    public UUID getId() {
        return id;
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

    public Set<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(Set<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}