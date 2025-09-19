package com.studybuddy.backend.entity.notebook;

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
@Document(collection = "notebooks")
public class Notebook {
    @Id
    private String id;
    @Indexed
    private String userId;
    @NotBlank(message = "Title cannot be blank.")
    private String title;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private Instant lastAccessedAt;
    private String color;
    private boolean isDeleted = false;
    private Instant deletedAt;

    public Notebook(String userId, String title) {
        this.userId = userId;
        this.title = title;
    }
}
