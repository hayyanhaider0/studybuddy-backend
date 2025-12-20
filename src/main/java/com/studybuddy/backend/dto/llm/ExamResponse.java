package com.studybuddy.backend.dto.llm;

import java.util.List;

import com.studybuddy.backend.dto.llm.embedded.ExamQuestion;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamResponse extends GenerateResponse {
    private List<ExamQuestion> questions;
}
