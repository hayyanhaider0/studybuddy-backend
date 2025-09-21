package com.studybuddy.backend.controller.notebook;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.CanvasCreateRequest;
import com.studybuddy.backend.dto.notebook.CanvasFetchRequest;
import com.studybuddy.backend.dto.notebook.CanvasResponse;
import com.studybuddy.backend.service.notebook.CanvasService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/canvases")
public class CanvasController {

    private final CanvasService canvasService;

    public CanvasController(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CanvasResponse>> createCanvas(@Valid @RequestBody CanvasCreateRequest req) {
        CanvasResponse data = canvasService.createCanvas(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data, null, "Canvas created successfully."));
    }

    @PostMapping("/by-chapters")
    public ResponseEntity<ApiResponse<Map<String, List<CanvasResponse>>>> getCanvasesByChapterIds(
            @Valid @RequestBody CanvasFetchRequest req) {

        if (req.getChapterIds() == null || req.getChapterIds().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, null, "chapterIds cannot be empty"));
        }

        Map<String, List<CanvasResponse>> data = canvasService.getCanvasesByChapterId(req.getChapterIds());
        return ResponseEntity.ok(new ApiResponse<>(true, data, null, "Canvases fetched successfully."));
    }
}
