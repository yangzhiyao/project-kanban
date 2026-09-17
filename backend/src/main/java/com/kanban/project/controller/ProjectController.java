package com.kanban.project.controller;

import com.kanban.project.dto.ProjectPageResponse;
import com.kanban.project.dto.ProjectResponse;
import com.kanban.project.model.ProjectStatus;
import com.kanban.project.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ProjectPageResponse list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) ProjectStatus status,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return projectService.list(keyword, status, page, size);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id) {
        return projectService.get(id);
    }
}
