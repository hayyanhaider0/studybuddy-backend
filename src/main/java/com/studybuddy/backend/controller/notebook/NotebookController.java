package com.studybuddy.backend.controller.notebook;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.ChapterResponse;
import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.service.notebook.ChapterService;
import com.studybuddy.backend.service.notebook.NotebookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notebooks")
public class NotebookController {
    private final NotebookService notebookService;
    private final ChapterService chapterService;

    public NotebookController(NotebookService notebookService, ChapterService chapterService) {
        this.notebookService = notebookService;
        this.chapterService = chapterService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotebookResponse>> createNotebook(@Valid @RequestBody NotebookRequest req) {
        NotebookResponse resData = notebookService.createNotebook(req);
        ApiResponse<NotebookResponse> res = new ApiResponse<>(true, resData, null, "Notebook created successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/{notebookId}/chapters")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapterInsideNotebook(@PathVariable String notebookId,
            @RequestBody ChapterRequest req) {
        ChapterResponse resData = chapterService.createChapter(notebookId, req);
        ApiResponse<ChapterResponse> res = new ApiResponse<ChapterResponse>(true, resData, null,
                "Chapter created successfully for notebook with id: " + notebookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotebookResponse>>> getNotebooks() {
        var resData = notebookService.getNotebooks();
        ApiResponse<List<NotebookResponse>> res = new ApiResponse<>(true, resData, null,
                "Notebooks fetched successfully.");
        return ResponseEntity.ok(res);
    }
}
