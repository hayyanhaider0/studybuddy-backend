package com.studybuddy.backend.controller.notebook;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.ChapterResponse;
import com.studybuddy.backend.service.notebook.ChapterService;
import com.studybuddy.backend.service.notebook.NotebookService;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {
    private final NotebookService notebookService;
    private final ChapterService chapterService;

    public ChapterController(NotebookService notebookService, ChapterService chapterService) {
        this.notebookService = notebookService;
        this.chapterService = chapterService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<ChapterResponse>>>> getChapters(
            @RequestParam(required = false) String notebookId,
            @RequestParam(required = false, defaultValue = "false") boolean recent,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        // Maximum limit for number of notebooks
        final int MAX_LIMIT = 30;
        limit = Math.min(limit, MAX_LIMIT);

        Map<String, List<ChapterResponse>> resData;

        if (recent) {
            List<String> recentIds = notebookService.getRecentNotebookIds(limit);
            resData = chapterService.getChaptersForRecentNotebooks(recentIds);
        } else if (notebookId != null) {
            resData = Map.of(notebookId, chapterService.getChaptersByNotebookId(notebookId));
        } else {
            throw new IllegalArgumentException("Either notebookId or recent=true must be specified.");
        }

        ApiResponse<Map<String, List<ChapterResponse>>> res = new ApiResponse<Map<String, List<ChapterResponse>>>(true,
                resData, null,
                "Chapters for the user's recent notebooks sent successfully.");
        return ResponseEntity.ok(res);
    }
}
