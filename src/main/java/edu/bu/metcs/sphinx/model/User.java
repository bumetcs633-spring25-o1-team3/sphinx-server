package edu.bu.metcs.sphinx.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    private String name;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<FlashcardSet> flashcardSets = new HashSet<>();

    public User() {
    }

    public User(UUID id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<FlashcardSet> getFlashcardSets() {
        return flashcardSets;
    }

    public void setFlashcardSets(Set<FlashcardSet> flashcardSets) {
        this.flashcardSets = flashcardSets;
    }
}
