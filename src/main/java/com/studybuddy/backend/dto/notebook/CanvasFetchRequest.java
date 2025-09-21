package com.studybuddy.backend.dto.notebook;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CanvasFetchRequest {
    private List<String> chapterIds;
}
