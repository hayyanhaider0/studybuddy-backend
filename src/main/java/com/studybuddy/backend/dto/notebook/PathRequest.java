package com.studybuddy.backend.dto.notebook;

import com.studybuddy.backend.enums.notebook.BrushType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import com.studybuddy.backend.entity.notebook.embedded.Point;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathRequest {

    private String canvasId;
    private List<Point> points;
    private BrushType brushType;
    private double baseWidth;
    private double opacity;
    private String color;
}
