package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.ChapterResponse;
import com.studybuddy.backend.entity.notebook.Chapter;
import com.studybuddy.backend.repository.ChapterRepository;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    // Create chapter
    public ChapterResponse createChapter(String notebookId, ChapterRequest req) {
        Chapter chapter = new Chapter(notebookId, req.getTitle(), req.getOrder());
        chapter = chapterRepository.save(chapter);
        return mapToResponse(chapter);
    }

    // Fetch chapters for multiple notebooks
    public List<ChapterResponse> getChaptersForRecentNotebooks(List<String> notebookIds) {
        return notebookIds.stream()
                .flatMap(id -> chapterRepository
                        .findAllByNotebookIdAndIsDeletedFalse(id)
                        .stream()
                        .map(this::mapToResponse))
                .toList();
    }

    // Fetch chapters by notebook
    public List<ChapterResponse> getChaptersByNotebookId(String notebookId) {
        List<Chapter> chapters = chapterRepository.findAllByNotebookIdAndIsDeletedFalse(notebookId);
        return chapters.stream().map(this::mapToResponse).toList();
    }

    private ChapterResponse mapToResponse(Chapter chapter) {
        ChapterResponse res = new ChapterResponse();
        res.setId(chapter.getId());
        res.setNotebookId(chapter.getNotebookId());
        res.setTitle(chapter.getTitle());
        res.setOrder(chapter.getOrder());
        res.setCreatedAt(chapter.getCreatedAt());
        res.setUpdatedAt(chapter.getUpdatedAt());
        return res;
    }
}
