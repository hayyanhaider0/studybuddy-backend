package com.studybuddy.backend.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.studybuddy.backend.entity.embedded.Point;
import com.studybuddy.backend.enums.BrushType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "paths")
public class Path {
    @Id
    private String id;
    @Indexed
    private String canvasId;
    @NotEmpty(message = "A path must have at least 1 point.")
    private List<Point> points;
    private BrushType brush;
    @Min(value = 1, message = "A path must have at least 1 point.")
    private int pointCount;

    public Path(String canvasId, List<Point> points, BrushType brush) {
        this.canvasId = canvasId;
        this.points = points;
        this.brush = brush;
        this.pointCount = points.size();
    }
}
