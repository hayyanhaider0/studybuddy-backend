package com.studybuddy.backend.dto.notebook;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PathCreateResponse {
    private String id;
    private String tempId;
}
