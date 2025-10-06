package com.studybuddy.backend.entity.notebook;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.studybuddy.backend.entity.notebook.embedded.Point;
import com.studybuddy.backend.enums.notebook.BrushType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    private String chapterId;
    @NotEmpty(message = "A path must have at least 1 point.")
    private List<Point> points;
    private BrushType brushType;
    @NotBlank
    @Min(value = 7, message = "Path color must be a hex code.")
    @Max(value = 7, message = "Path color must be a hex code.")
    private String color;
    @Min(value = 0, message = "Base width must be a positive integer.")
    private double baseWidth;
    @Range(min = 0, max = 1, message = "Opacity must be between 0 and 1.")
    private double opacity;
    @Min(value = 1, message = "A path must have at least 1 point.")
    private int pointCount;

    public Path(String canvasId, List<Point> points, BrushType brushType, String color, double baseWidth,
            double opacity) {
        this.canvasId = canvasId;
        this.points = points;
        this.brushType = brushType;
        this.color = color;
        this.baseWidth = baseWidth;
        this.opacity = opacity;
        this.pointCount = points.size();
    }
}
