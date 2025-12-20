package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.CanvasCreateRequest;
import com.studybuddy.backend.dto.notebook.CanvasResponse;
import com.studybuddy.backend.dto.notebook.CanvasUpdateRequest;
import com.studybuddy.backend.entity.notebook.Canvas;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.repository.CanvasRepository;
import com.studybuddy.backend.utility.auth.AuthUtil;

@Service
public class CanvasService {

    private final CanvasRepository canvasRepository;
    private final AuthUtil authUtil;

    public CanvasService(CanvasRepository canvasRepository, AuthUtil authUtil) {
        this.canvasRepository = canvasRepository;
        this.authUtil = authUtil;
    }

    // Create canvas
    public CanvasResponse createCanvas(CanvasCreateRequest req) {
        authUtil.getCurrentUserId();

        Canvas canvas = new Canvas(req.getChapterId(), req.getNotebookId(), req.getOrder());
        canvas = canvasRepository.save(canvas);
        return mapToResponse(canvas);
    }

    // Update canvas
    public void updateCanvas(String id, CanvasUpdateRequest req) {
        authUtil.getCurrentUserId();

        Canvas canvas = canvasRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Canvas with ID " + id + " not found."));

        if (req.getOrder() != -1) {
            canvas.setOrder(req.getOrder());
        }

        if (req.getColor() != null) {
            canvas.setColor(req.getColor());
        }

        if (req.getPattern() != null) {
            canvas.setPattern(req.getPattern());
        }

        canvasRepository.save(canvas);
    }

    // Fetch canvases by chapter IDs
    public List<CanvasResponse> getCanvasesByChapterId(List<String> chapterIds) {
        authUtil.getCurrentUserId();

        List<Canvas> canvases = canvasRepository.findAllByChapterIdInAndIsDeletedFalse(chapterIds);

        return canvases.stream().map(this::mapToResponse).toList();
    }

    public void deleteCanvas(String id) {
        authUtil.getCurrentUserId();
        canvasRepository.deleteById(id);
    }

    private CanvasResponse mapToResponse(Canvas canvas) {
        CanvasResponse res = new CanvasResponse();
        res.setId(canvas.getId());
        res.setChapterId(canvas.getChapterId());
        res.setOrder(canvas.getOrder());
        return res;
    }
}
