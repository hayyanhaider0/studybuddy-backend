package com.studybuddy.backend.controller.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.SyncRequest;
import com.studybuddy.backend.service.notebook.SyncService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/sync")
public class SyncController<T> {
    private SyncService<T> syncService;

    public SyncController(SyncService<T> syncService) {
        this.syncService = syncService;
    }

    public ResponseEntity<ApiResponse<Void>> sync(@RequestBody List<SyncRequest<T>> req) {
        syncService.sync(req);
        return ResponseEntity.ok(new ApiResponse<Void>(true, null, null, "State synced successfully."));
    }
}
