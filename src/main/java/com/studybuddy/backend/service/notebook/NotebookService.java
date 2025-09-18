package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.NotebookResponse;
import com.studybuddy.backend.entity.notebook.Notebook;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.repository.NotebookRepository;
import com.studybuddy.backend.repository.UserRepository;

@Service
public class NotebookService {
    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;

    public NotebookService(NotebookRepository notebookRepository, UserRepository userRepository) {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
    }

    public NotebookResponse createNotebook(NotebookRequest req) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Notebook notebook = new Notebook(userId, req.getTitle());

        if (req.getColor() != null && !req.getColor().trim().isEmpty()) {
            notebook.setColor(req.getColor());
        }

        notebook = notebookRepository.save(notebook);

        return mapToResponse(notebook);
    }

    public List<NotebookResponse> getNotebooks() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<Notebook> notebooks = notebookRepository.findByUserIdAndIsDeletedFalse(userId);

        return notebooks.stream().map(this::mapToResponse).toList();
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
