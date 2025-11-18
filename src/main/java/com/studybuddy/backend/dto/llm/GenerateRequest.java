package com.studybuddy.backend.dto.llm;

import java.util.List;
import java.util.Map;

import com.studybuddy.backend.dto.llm.embedded.ChaptersWithCanvases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateRequest {
    private String taskType;
    private String occupation;
    private String educationLevel;
    private String notebookName;
    private List<ChaptersWithCanvases> chaptersWithCanvases;
    private Map<String, Object> options;
}
