package com.kanban.project.service;

import com.kanban.project.dto.ProjectPageResponse;
import com.kanban.project.dto.ProjectResponse;
import com.kanban.project.dto.ProjectSummary;
import com.kanban.project.exception.NotFoundException;
import com.kanban.project.model.Project;
import com.kanban.project.model.ProjectStatus;
import com.kanban.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public ProjectPageResponse list(String keyword, ProjectStatus status, int page, int size) {
        String normalizedKeyword = normalize(keyword);
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.clamp(size, 1, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Project> result = repository.search(normalizedKeyword, status, pageRequest);
        ProjectSummary summary = repository.summarize(normalizedKeyword, status);
        List<ProjectResponse> items = result.getContent().stream().map(ProjectService::toResponse).toList();

        return new ProjectPageResponse(items, result.getTotalElements(), result.getNumber(), result.getSize(), summary);
    }

    public ProjectResponse get(Long id) {
        return toResponse(findActive(id));
    }

    private Project findActive(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("项目不存在: " + id));
    }

    private static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getCode(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getPriority(),
                project.getOwner(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProgress(),
                project.getReceivableAmount(),
                project.getPayableAmount(),
                project.getReceivedAmount(),
                project.getPaidAmount(),
                project.getReceivableAmount().subtract(project.getPayableAmount()),
                project.getReceivedAmount().subtract(project.getPaidAmount()),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    private static String normalize(String keyword) {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }
}
