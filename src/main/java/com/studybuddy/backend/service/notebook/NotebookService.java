package com.studybuddy.backend.service.notebook;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.entity.notebook.Notebook;
import com.studybuddy.backend.repository.NotebookRepository;

@Service
public class NotebookService {
    private final NotebookRepository notebookRepository;

    public NotebookService(NotebookRepository notebookRepository) {
        this.notebookRepository = notebookRepository;
    }

    public NotebookResponse createNotebook(NotebookRequest req) {
        Notebook notebook = new Notebook(req.getUserId(), req.getTitle());

        if (req.getColor() != null && !req.getColor().trim().isEmpty()) {
            notebook.setColor(req.getColor());
        }

        notebookRepository.save(notebook);

        return mapToResponse(notebook);
    }

    private NotebookResponse mapToResponse(Notebook notebook) {
        NotebookResponse res = new NotebookResponse();
        res.setId(notebook.getId());
        res.setTitle(notebook.getTitle());
        res.setColor(notebook.getColor());
        res.setCreatedAt(notebook.getCreatedAt());
        res.setDeleted(notebook.isDeleted());
        return res;
    }
}
