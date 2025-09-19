package com.studybuddy.backend.controller.notebook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
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
        ChapterResponse resData = chapterService.createChapter(req);
        ApiResponse<ChapterResponse> res = new ApiResponse<ChapterResponse>(true, resData, null,
                "Chapter created successfully.");
        return ResponseEntity.ok(res);
    }
}
