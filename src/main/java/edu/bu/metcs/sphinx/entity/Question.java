package edu.bu.metcs.sphinx.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;

    public Question() {}

    public Question(String text, Quiz quiz) {
        this.text = text;
        this.quiz = quiz;
    }

    public UUID getId() { return id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
