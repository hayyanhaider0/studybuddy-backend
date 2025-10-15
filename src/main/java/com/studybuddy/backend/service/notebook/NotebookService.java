package com.studybuddy.backend.service.notebook;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.entity.notebook.Canvas;
import com.studybuddy.backend.entity.notebook.Chapter;
import com.studybuddy.backend.entity.notebook.Notebook;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.repository.CanvasRepository;
import com.studybuddy.backend.repository.ChapterRepository;
import com.studybuddy.backend.repository.NotebookRepository;
import com.studybuddy.backend.utility.auth.AuthUtil;

@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final ChapterRepository chapterRepository;
    private final CanvasRepository canvasRepository;
    private final ChapterService chapterService;
    private final AuthUtil authUtil;

    public NotebookService(NotebookRepository notebookRepository, ChapterRepository chapterRepository,
            CanvasRepository canvasRepository, ChapterService chapterService,
            AuthUtil authUtil) {
        this.notebookRepository = notebookRepository;
        this.chapterRepository = chapterRepository;
        this.canvasRepository = canvasRepository;
        this.chapterService = chapterService;
        this.authUtil = authUtil;
    }

    // Create notebook
    public NotebookResponse createNotebook(NotebookRequest req) {
        String userId = authUtil.getCurrentUserId();

        Notebook notebook = new Notebook(userId, req.getTitle());
        if (req.getColor() != null && !req.getColor().trim().isEmpty()) {
            notebook.setColor(req.getColor());
        }
        notebook = notebookRepository.save(notebook);

        // Create first chapter automatically
        ChapterRequest chapterReq = new ChapterRequest(notebook.getId(), "Chapter 1", 0);
        var firstChapter = chapterService.createChapter(notebook.getId(), chapterReq);

        NotebookResponse res = mapToResponse(notebook);
        res.setChapters(List.of(firstChapter));

        return res;
    }

    // Fetch all notebooks for user
    public List<NotebookResponse> getNotebooks() {
        String userId = authUtil.getCurrentUserId();

        List<Notebook> notebooks = notebookRepository.findByUserIdAndIsDeletedFalse(userId);
        return notebooks.stream().map(this::mapToResponse).toList();
    }

    // Fetch recent notebook IDs
    public List<String> getRecentNotebookIds(int limit) {
        String userId = authUtil.getCurrentUserId();
        limit = Math.min(limit, 30); // Max limit

        List<Notebook> notebooks = notebookRepository
                .findByUserIdAndIsDeletedFalseOrderByLastAccessedAtDesc(userId, PageRequest.of(0, limit));

        return notebooks.stream().map(Notebook::getId).toList();
    }

    public void deleteNotebook(String id) {
        String userId = authUtil.getCurrentUserId();
        Notebook notebook = notebookRepository.findByIdAndUserIdAndIsDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notebook  with id " + id + " not found."));

        notebook.setDeleted(true);
        notebook.setDeletedAt(Instant.now());

        notebookRepository.save(notebook);

        cascaseSoftDelete(notebook);
    }

    private void cascaseSoftDelete(Notebook notebook) {
        Instant now = Instant.now();

        List<Chapter> chapters = chapterRepository.findAllByNotebookIdAndIsDeletedFalse(notebook.getId());
        for (Chapter chapter : chapters) {
            chapter.setDeleted(true);
            chapter.setDeletedAt(now);
        }

        chapterRepository.saveAll(chapters);
        List<String> chapterIds = chapters.stream().map(Chapter::getId).collect(Collectors.toList());

        List<Canvas> canvases = canvasRepository.findAllByChapterIdInAndIsDeletedFalse(chapterIds);
        for (Canvas canvas : canvases) {
            canvas.setDeleted(true);
            canvas.setDeletedAt(now);
        }

        canvasRepository.saveAll(canvases);
    }

    private NotebookResponse mapToResponse(Notebook notebook) {
        NotebookResponse res = new NotebookResponse();
        res.setId(notebook.getId());
        res.setTitle(notebook.getTitle());
        res.setColor(notebook.getColor());
        res.setCreatedAt(notebook.getCreatedAt());
        res.setUpdatedAt(notebook.getUpdatedAt());
        res.setDeleted(notebook.isDeleted());
        return res;
    }
}
