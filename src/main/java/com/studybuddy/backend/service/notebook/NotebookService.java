package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.entity.notebook.Notebook;
import com.studybuddy.backend.repository.NotebookRepository;
import com.studybuddy.backend.repository.UserRepository;
import com.studybuddy.backend.utility.auth.AuthUtil;

@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final ChapterService chapterService;
    private final AuthUtil authUtil;

    public NotebookService(NotebookRepository notebookRepository, UserRepository userRepository,
            ChapterService chapterService, AuthUtil authUtil) {
        this.notebookRepository = notebookRepository;
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
        chapterService.createChapter(notebook.getId(), chapterReq);

        return mapToResponse(notebook);
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
