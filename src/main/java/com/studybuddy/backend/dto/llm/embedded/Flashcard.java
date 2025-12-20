package com.studybuddy.backend.dto.llm.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flashcard {
    private String question;
    private String answer;
}
