package com.studybuddy.backend.controller.notebook;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.service.notebook.NotebookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notebooks")
public class NotebookController {

    private final NotebookService notebookService;

    public NotebookController(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotebookResponse>> createNotebook(@Valid @RequestBody NotebookRequest req) {
        NotebookResponse data = notebookService.createNotebook(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data, null, "Notebook created successfully."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotebookResponse>>> getNotebooks() {
        List<NotebookResponse> data = notebookService.getNotebooks();
        return ResponseEntity.ok(new ApiResponse<>(true, data, null, "Notebooks fetched successfully."));
    }
}
