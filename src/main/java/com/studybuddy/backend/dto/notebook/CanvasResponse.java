package com.studybuddy.backend.dto.notebook;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CanvasResponse {
    private String id;
    private String chapterId;
    private String notebookId;
    private String color;
    private String pattern;
    private int order;
}
