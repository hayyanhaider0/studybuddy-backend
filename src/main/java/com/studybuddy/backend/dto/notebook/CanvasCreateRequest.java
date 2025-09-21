package com.studybuddy.backend.dto.notebook;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CanvasCreateRequest {
    private String chapterId;
    private int order;
}
