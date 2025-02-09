package edu.bu.metcs.sphinx.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private int score;

    public QuizAttempt() {}

    public QuizAttempt(Quiz quiz, int score) {
        this.quiz = quiz;
        this.score = score;
    }

    public UUID getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
