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
public class CanvasCreateRequest {
    @NotBlank
    private String chapterId;
    @NotNull
    @Min(0)
    @Max(100)
    private int order;
}
