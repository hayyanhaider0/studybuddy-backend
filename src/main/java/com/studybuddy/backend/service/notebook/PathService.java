package com.studybuddy.backend.service.notebook;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.notebook.PathRequest;
import com.studybuddy.backend.dto.notebook.PathResponse;
import com.studybuddy.backend.entity.notebook.Path;
import com.studybuddy.backend.repository.PathRepository;

@Service
public class PathService {
    private final PathRepository pathRepository;

    public PathService(PathRepository pathRepository)
    {
        this.pathRepository = pathRepository;
    }

    public void createPath(PathRequest req)
    {
        Path path = new Path(req.getCanvasId(),req.getPoints(),req.getBrushType());
        pathRepository.save(path);
    }

    public List<PathResponse> getPathsByChapterId(String chapterId)
    {
        return pathRepository.findAllByChapterId(chapterId).stream().map(this::mapToResponse).toList();
    }

    private PathResponse mapToResponse(Path path)
    {
        return new PathResponse(path.getCanvasId(), path.getPoints(), path.getBrushType(), path.getBaseWidth(),path.getOpacity(), path.getColor());
    }
}
