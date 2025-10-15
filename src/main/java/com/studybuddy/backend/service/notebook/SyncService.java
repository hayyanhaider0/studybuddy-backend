package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.NotebookRequest;
import com.studybuddy.backend.dto.notebook.SyncRequest;
import com.studybuddy.backend.exception.InvalidRequestException;

@Service
public class SyncService<T> {
    private NotebookService notebookService;

    public SyncService(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    public void sync(List<SyncRequest<T>> reqs) {
        for (SyncRequest<T> req : reqs) {
            switch (req.getType()) {
                case "CREATE_NOTEBOOK":
                    if (!(req.getPayload() instanceof NotebookRequest)) {
                        throw new InvalidRequestException(
                                "[service/SYNC_SERVICE]: Payload is not of type NOTEBOOK_REQUEST");
                    }

                    notebookService.createNotebook((NotebookRequest) req.getPayload());
                    break;
                case "UPDATE_NOTEBOOK":
                case "DELETE_NOTEBOOK":
                case "CREATE_CHAPTER":
                case "UPDATE_CHAPTER":
                case "DELETE_CHAPTER":
                case "CREATE_CANVAS":
                case "UPDATE_CANVAS":
                case "DELETE_CANVAS":
                case "CREATE_PATH":
                case "UPDATE_PATH":
                case "DELETE_PATH":
            }
        }
    }
}
