package com.studybuddy.backend.dto.notebook;

import com.mongodb.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotebookRequest {
    @NotBlank(message = "Title cannot be blank.")
    private String title;
    @Nullable
    @Size(min = 7, max = 7, message = "Invalid color detected.")
    private String color;
}
