package com.studybuddy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studybuddy.backend.entity.notebook.Canvas;

public interface CanvasRepository extends MongoRepository<Canvas, String> {
    Optional<Canvas> findByIdAndIsDeletedFalse(String id);

    List<Canvas> findAllByChapterIdInAndIsDeletedFalse(List<String> chapterId);
}
