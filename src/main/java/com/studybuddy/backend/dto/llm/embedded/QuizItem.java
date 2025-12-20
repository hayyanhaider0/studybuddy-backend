package com.studybuddy.backend.dto.llm.embedded;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizItem {
    private String question;
    private List<String> options;
    private String answer;
    private String explanation;
}
