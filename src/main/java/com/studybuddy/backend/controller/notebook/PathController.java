package com.studybuddy.backend.controller.notebook;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.notebook.PathRequest;
import com.studybuddy.backend.dto.notebook.PathResponse;
import com.studybuddy.backend.service.notebook.PathService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/paths")
public class PathController {
    
    private final PathService pathService;

    public PathController(PathService pathService)
    {
        this.pathService = pathService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPath(@Valid @RequestBody PathRequest req) {
        pathService.createPath(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<Void>(true, null, null, "Path created successfully."));
    }

    @GetMapping("/by-chapter")
    public ResponseEntity<ApiResponse<List<PathResponse>>> getPathsByChapterId(@RequestParam String chapterId)
    {
        List<PathResponse> resData = pathService.getPathsByChapterId(chapterId);
        return ResponseEntity.ok(new ApiResponse<List<PathResponse>>(true,resData , null, "Paths fetched successfully."));
    }


}
    


