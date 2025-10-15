package com.studybuddy.backend.service.notebook;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.PathCreateResponse;
import com.studybuddy.backend.dto.notebook.PathRequest;
import com.studybuddy.backend.dto.notebook.PathResponse;
import com.studybuddy.backend.entity.notebook.Path;
import com.studybuddy.backend.repository.PathRepository;

@Service
public class PathService {
    private final PathRepository pathRepository;

    public PathService(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    public List<PathCreateResponse> createPaths(List<PathRequest> req) {
        List<PathCreateResponse> resData = new ArrayList<>();

        for (PathRequest r : req) {
            Path path = new Path(r.getCanvasId(), r.getPoints(), r.getBrushType(), r.getColor(),
                    r.getBaseWidth(), r.getOpacity());
            Path pathWithId = pathRepository.save(path);
            resData.add(new PathCreateResponse(pathWithId.getId(), r.getTempId()));
        }

        return resData;
    }

    public List<PathResponse> getPathsByCanvasIds(List<String> canvasIds) {
        return pathRepository.findAllByCanvasIdIn(canvasIds).stream().map(this::mapToResponse).toList();
    }

    public List<PathResponse> getPathsByChapterId(String chapterId) {
        return pathRepository.findAllByChapterId(chapterId).stream().map(this::mapToResponse).toList();
    }

    public void deletePathsById(List<String> ids) {
        pathRepository.deleteAllById(ids);
    }

    private PathResponse mapToResponse(Path path) {
        return new PathResponse(path.getId(), path.getCanvasId(), path.getPoints(), path.getBrushType(),
                path.getBaseWidth(),
                path.getOpacity(), path.getColor());
    }
}
