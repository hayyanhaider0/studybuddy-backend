package com.studybuddy.backend.dto.llm;

import java.util.List;

import com.studybuddy.backend.dto.llm.embedded.Flashcard;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlashcardsResponse extends GenerateResponse {
    private List<Flashcard> flashcards;
}
