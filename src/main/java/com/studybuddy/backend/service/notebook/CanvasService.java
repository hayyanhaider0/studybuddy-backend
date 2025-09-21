package com.studybuddy.backend.service.notebook;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.CanvasResponse;
import com.studybuddy.backend.entity.notebook.Canvas;
import com.studybuddy.backend.repository.CanvasRepository;

@Service
public class CanvasService {
    private final CanvasRepository canvasRepository;

    public CanvasService(CanvasRepository canvasRepository) {
        this.canvasRepository = canvasRepository;
    }

    public Map<String, List<CanvasResponse>> getCanvasesByChapterId(List<String> chapterIds) {
        List<Canvas> canvases = canvasRepository.findAllByChapterIdInAndIsDeletedFalse(chapterIds);
        List<CanvasResponse> canvasResponses = canvases.stream().map(this::mapToResponse).toList();

        return canvasResponses.stream().collect(Collectors.groupingBy(CanvasResponse::getChapterId));
    }

    private CanvasResponse mapToResponse(Canvas canvas) {
        CanvasResponse canvasResponse = new CanvasResponse();
        canvasResponse.setId(canvas.getId());
        canvasResponse.setChapterId(canvas.getChapterId());
        return canvasResponse;
    }
}
