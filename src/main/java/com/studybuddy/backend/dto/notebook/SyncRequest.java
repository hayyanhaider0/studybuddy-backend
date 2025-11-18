package com.studybuddy.backend.dto.notebook;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SyncRequest<T> {
    private String type;
    private T payload;
    private Instant timestamp;
}
