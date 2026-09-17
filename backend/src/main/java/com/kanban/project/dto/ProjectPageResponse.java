package com.kanban.project.dto;

import java.util.List;

public record ProjectPageResponse(
        List<ProjectResponse> items,
        long total,
        int page,
        int size,
        ProjectSummary summary) {
}
