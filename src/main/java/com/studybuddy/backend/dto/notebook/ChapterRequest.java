package com.studybuddy.backend.dto.notebook;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterRequest {
    @NotBlank
    String notebookId;
    @NotBlank(message = "Title cannot be blank.")
    String title;
    @NotBlank
    @Size(min = 0, message = "Order must be non-negative.")
    int order;
}
