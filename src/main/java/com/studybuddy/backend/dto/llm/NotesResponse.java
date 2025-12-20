package com.studybuddy.backend.dto.llm;

import java.util.List;

import com.studybuddy.backend.dto.llm.embedded.ChaptersWithCanvases;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotesResponse extends GenerateResponse {
    private List<ChaptersWithCanvases> notes;
}
