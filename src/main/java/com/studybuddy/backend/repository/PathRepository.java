package com.studybuddy.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studybuddy.backend.entity.notebook.Path;

public interface PathRepository extends MongoRepository<Path, String> {
    
    List<Path> findAllByChapterId(String chapterId);
}
