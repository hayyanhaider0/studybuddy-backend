package com.studybuddy.backend.dto.llm;

import java.util.List;

import com.studybuddy.backend.dto.llm.embedded.QuizItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuizResponse extends GenerateResponse {
    private List<QuizItem> item;
}
