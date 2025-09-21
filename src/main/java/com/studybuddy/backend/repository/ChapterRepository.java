package com.studybuddy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studybuddy.backend.entity.notebook.Chapter;

public interface ChapterRepository extends MongoRepository<Chapter, String> {
    Optional<Chapter> findByIdAndIsDeletedFalse(String id);

    List<Chapter> findAllByNotebookIdAndIsDeletedFalse(String notebookId);
}
