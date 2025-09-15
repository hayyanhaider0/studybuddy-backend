package com.studybuddy.backend.entity.notebook.embedded;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {
    private float x;
    private float y;
    @Range(min = 0, max = 1, message = "Pressure must be between 0 and 1.")
    private float pressure;
}
