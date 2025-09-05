package com.studybuddy.backend.dto.notebook;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotebookRequest {
    @NotBlank(message = "UserId cannot be blank.")
    private String userId;
    @NotBlank(message = "Title cannot be blank.")
    private String title;
    private String color;
}
