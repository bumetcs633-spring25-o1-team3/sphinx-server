package edu.bu.metcs.sphinx.dto;

import edu.bu.metcs.sphinx.model.Flashcard;
import java.util.UUID;

public class FlashcardDTO {
    private UUID id;
    private String question;
    private String answer;
    private boolean createReverse;

    public FlashcardDTO() {}

    public FlashcardDTO(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCreateReverse() {
        return createReverse;
    }

    public void setCreateReverse(boolean createReverse) {
        this.createReverse = createReverse;
    }

    public static FlashcardDTO fromEntity(Flashcard entity) {
        FlashcardDTO dto = new FlashcardDTO();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setAnswer(entity.getAnswer());
        return dto;
    }
}