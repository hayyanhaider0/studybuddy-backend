package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.ChapterRequest;
import com.studybuddy.backend.dto.notebook.ChapterResponse;
import com.studybuddy.backend.entity.notebook.Chapter;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.repository.ChapterRepository;

@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public ChapterResponse createChapter(ChapterRequest req) {
        Chapter chapter = new Chapter(
                req.getNotebookId(),
                req.getTitle(),
                req.getOrder());

        chapter = chapterRepository.save(chapter);

        return mapToResponse(chapter);
    }

    public List<Chapter> getChaptersByNotebookId(String notebookId) {
        List<Chapter> chapters = chapterRepository.findAllByNotebookIdAndIsDeletedFalse(notebookId).orElseThrow(
                () -> new ResourceNotFoundException("No chapters found for notebook with id: " + notebookId));

        return chapters;
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
