package com.studybuddy.backend.dto.notebook;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotebookResponse {
    private String id;
    private String title;
    private String color;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDeleted;
}
