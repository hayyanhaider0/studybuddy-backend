package com.studybuddy.backend.dto.llm.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestion {
    private String question;
    private int marks;
    private String answer;
}
