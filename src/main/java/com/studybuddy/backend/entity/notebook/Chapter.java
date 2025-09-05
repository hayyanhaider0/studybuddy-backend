package com.studybuddy.backend.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "chapters")
public class Chapter {
    @Id
    private String id;
    @Indexed
    private String notebookId;
    @NotBlank(message = "Title cannot be blank.")
    private String title;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private int order;
    private boolean isDeleted = false;
    private Instant deletedAt;

    public Chapter(String notebookId, String title, int order) {
        this.notebookId = notebookId;
        this.title = title;
        this.order = order;
    }
}
