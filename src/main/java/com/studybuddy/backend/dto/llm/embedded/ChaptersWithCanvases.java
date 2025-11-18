package com.studybuddy.backend.dto.llm.embedded;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChaptersWithCanvases {
    private String chapterName;
    private List<String> canvases;
}
