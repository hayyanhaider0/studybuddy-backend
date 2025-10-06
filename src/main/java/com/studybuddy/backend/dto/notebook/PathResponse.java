package com.studybuddy.backend.dto.notebook;

import java.util.List;

import com.studybuddy.backend.entity.notebook.embedded.Point;
import com.studybuddy.backend.enums.notebook.BrushType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathResponse {
    private String id;
    private String canvasId;
    private List<Point> points;
    private BrushType brushType;
    private double baseWidth;
    private double opacity;
    private String color;
}
