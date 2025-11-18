package com.studybuddy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.studybuddy.backend.entity.notebook.Notebook;

public interface NotebookRepository extends MongoRepository<Notebook, String> {
    Optional<Notebook> findByIdAndUserIdAndIsDeletedFalse(String id, String userId);

    List<Notebook> findByUserIdAndIsDeletedFalse(String id);

    List<Notebook> findByUserIdAndIsDeletedFalseOrderByLastAccessedAtDesc(String id, Pageable pageable);
}
