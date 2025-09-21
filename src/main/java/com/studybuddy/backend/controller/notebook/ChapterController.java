package com.studybuddy.backend.controller.notebook;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.ChapterFetchRequest;
import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.ChapterResponse;
import com.studybuddy.backend.service.notebook.ChapterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(@Valid @RequestBody ChapterRequest req) {
        ChapterResponse data = chapterService.createChapter(req.getNotebookId(), req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data, null, "Chapter created successfully."));
    }

    @PostMapping("/by-notebooks")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getChaptersByNotebookIds(
            @Valid @RequestBody ChapterFetchRequest req) {

        List<ChapterResponse> data = chapterService.getChaptersForRecentNotebooks(req.getNotebookIds());
        return ResponseEntity.ok(new ApiResponse<>(true, data, null, "Chapters fetched successfully."));
    }
}
