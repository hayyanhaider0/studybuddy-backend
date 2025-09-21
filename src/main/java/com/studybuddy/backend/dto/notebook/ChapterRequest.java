package com.studybuddy.backend.dto.notebook;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequest {
    @NotBlank
    String notebookId;
    @NotBlank(message = "Title cannot be blank.")
    String title;
    @NotNull
    @Min(0)
    @Max(100)
    int order;
}
