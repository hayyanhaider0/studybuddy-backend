package com.studybuddy.backend.dto.notebook;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanvasUpdateRequest {
    @NotEmpty
    private List<String> ids;
    private String color;
    private String pattern;
    @Min(-1)
    @Max(100)
    private Integer order;
}
