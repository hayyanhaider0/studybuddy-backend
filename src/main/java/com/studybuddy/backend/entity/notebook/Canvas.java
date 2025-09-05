package com.studybuddy.backend.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "canvases")
public class Canvas {
    @Id
    private String id;
    @Indexed
    private String chapterId;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private int order;
    private boolean isDeleted = false;
    private Instant deletedAt;

    public Canvas(String chapterId, int order) {
        this.chapterId = chapterId;
        this.order = order;
    }
}
