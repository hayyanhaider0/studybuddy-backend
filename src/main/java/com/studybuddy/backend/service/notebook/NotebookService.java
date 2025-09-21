package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.entity.notebook.Notebook;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.repository.NotebookRepository;
import com.studybuddy.backend.repository.UserRepository;
import com.studybuddy.backend.utility.auth.AuthUtil;

@Service
public class NotebookService {
    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;
    private final ChapterService chapterService;
    private final AuthUtil authUtil;

    public NotebookService(NotebookRepository notebookRepository, UserRepository userRepository,
            ChapterService chapterService, AuthUtil authUtil) {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
        this.chapterService = chapterService;
        this.authUtil = authUtil;
    }

    public NotebookResponse createNotebook(NotebookRequest req) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Notebook notebook = new Notebook(userId, req.getTitle());

        if (req.getColor() != null && !req.getColor().trim().isEmpty()) {
            notebook.setColor(req.getColor());
        }

        notebook = notebookRepository.save(notebook);

        // Create the first chapter
        String notebookId = notebook.getId();
        ChapterRequest chapterRequest = new ChapterRequest(notebookId, "Chapter 1", 0);
        chapterService.createChapter(notebookId, chapterRequest);

        return mapToResponse(notebook);
    }

    public List<NotebookResponse> getNotebooks() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        List<Notebook> notebooks = notebookRepository.findByUserIdAndIsDeletedFalse(userId);
        return notebooks.stream().map(this::mapToResponse).toList();
    }

    public List<String> getRecentNotebookIds(int limit) {
        String userId = authUtil.getCurrentUserId();

        // Enforce max limit
        final int MAX_LIMIT = 30;
        limit = Math.min(limit, MAX_LIMIT);

        Pageable pageable = PageRequest.of(0, limit);
        List<Notebook> notebooks = notebookRepository.findByUserIdAndIsDeletedFalseOrderByLastAccessedDesc(userId,
                pageable);
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
