package com.studybuddy.backend.dto.notebook;

import java.time.Instant;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterResponse {
    private String id;
    private String notebookId;
    private String title;
    private int order;
    private Instant createdAt;
    private Instant updatedAt;

    private List<CanvasResponse> canvases;
}
