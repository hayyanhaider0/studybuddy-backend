package com.studybuddy.backend.controller.llm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.llm.GenerateRequest;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.service.llm.LLMService;

@RestController
@RequestMapping("/api/ai")
public class LLMController {
    private LLMService llmService;

    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Void>> generate(@RequestBody GenerateRequest req) {
        String taskType = req.getTaskType();

        switch (taskType) {
            case "notes":
                llmService.generateNotes();
                break;
            case "flashcards":
                llmService.generateFlashcards();
                break;
            case "quiz":
                llmService.generateQuiz();
                break;
            case "exam":
                llmService.generateExam();
                break;
            default:
                throw new ResourceNotFoundException("Task type: " + taskType + " not found!");
        }

        return ResponseEntity.ok(new ApiResponse<Void>(true, null, null, taskType));
    }
}
