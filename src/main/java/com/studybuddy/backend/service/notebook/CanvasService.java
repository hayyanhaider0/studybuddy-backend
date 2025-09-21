package com.studybuddy.backend.service.notebook;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.CanvasCreateRequest;
import com.studybuddy.backend.dto.notebook.CanvasResponse;
import com.studybuddy.backend.entity.notebook.Canvas;
import com.studybuddy.backend.repository.CanvasRepository;

@Service
public class CanvasService {

    private final CanvasRepository canvasRepository;

    public CanvasService(CanvasRepository canvasRepository) {
        this.canvasRepository = canvasRepository;
    }

    // Create canvas
    public CanvasResponse createCanvas(CanvasCreateRequest req) {
        Canvas canvas = new Canvas(req.getChapterId(), req.getOrder());
        canvas = canvasRepository.save(canvas);
        return mapToResponse(canvas);
    }

    // Fetch canvases by chapter IDs
    public Map<String, List<CanvasResponse>> getCanvasesByChapterId(List<String> chapterIds) {
        List<Canvas> canvases = canvasRepository.findAllByChapterIdInAndIsDeletedFalse(chapterIds);
        List<CanvasResponse> responses = canvases.stream().map(this::mapToResponse).toList();

        return responses.stream()
                .collect(Collectors.groupingBy(CanvasResponse::getChapterId));
    }

    private CanvasResponse mapToResponse(Canvas canvas) {
        CanvasResponse res = new CanvasResponse();
        res.setId(canvas.getId());
        res.setChapterId(canvas.getChapterId());
        res.setOrder(canvas.getOrder());
        return res;
    }
}
