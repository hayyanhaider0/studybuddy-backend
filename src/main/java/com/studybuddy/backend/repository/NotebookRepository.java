package com.studybuddy.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.studybuddy.backend.entity.notebook.Notebook;

public interface NotebookRepository extends MongoRepository<Notebook, String> {
    List<Notebook> findByUserIdAndIsDeletedFalse(String id);
}
