package com.studybuddy.backend.dto.notebook;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanvasUpdateRequest {
    private String id;
    private String color;
    private String pattern;
    @Min(-1)
    @Max(100)
    private int order;
}
