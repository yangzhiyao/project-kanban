package com.kanban.project.service;

import com.kanban.project.dto.ProjectPageResponse;
import com.kanban.project.dto.ProjectRequest;
import com.kanban.project.dto.ProjectResponse;
import com.kanban.project.dto.ProjectSummary;
import com.kanban.project.exception.DuplicateCodeException;
import com.kanban.project.exception.InvalidRequestException;
import com.kanban.project.exception.NotFoundException;
import com.kanban.project.model.Project;
import com.kanban.project.model.ProjectStatus;
import com.kanban.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 项目业务逻辑。
 *
 * <p>职责：DTO 与实体互转、编号唯一性校验、金额归一化（{@code null} 视为 0，保留 2 位小数）、
 * 跨字段日期校验以及软删除。类级为只读事务，写方法单独标注 {@link Transactional}。
 */
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * 分页查询项目，并按相同条件统计金额合计。
     *
     * @param keyword 可选，忽略大小写模糊匹配编号或名称
     * @param status  可选，按状态精确过滤
     * @param page    页码，从 0 开始，负数按 0 处理
     * @param size    每页条数，自动收敛到 1..{@value #MAX_PAGE_SIZE}
     * @return 分页数据与合计；固定按更新时间倒序
     */
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

    /**
     * 查询未删除的项目详情。
     *
     * @throws NotFoundException 项目不存在或已删除
     */
    public ProjectResponse get(Long id) {
        return toResponse(findActive(id));
    }

    /**
     * 新建项目。
     *
     * @throws DuplicateCodeException 编号已被占用
     * @throws InvalidRequestException 日期区间不合法
     */
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        String code = normalizeCode(request.code());
        if (repository.existsByCodeAndDeletedFalse(code)) {
            throw new DuplicateCodeException("项目编号已存在: " + code);
        }

        Project project = new Project();
        apply(project, request, code);
        return toResponse(repository.saveAndFlush(project));
    }

    /**
     * 全量更新项目（未提交的可选字段按默认值覆盖）。
     *
     * <p>使用 {@code saveAndFlush} 立即写库，确保返回体中的 {@code updatedAt} 已刷新。
     *
     * @throws NotFoundException 项目不存在或已删除
     * @throws DuplicateCodeException 编号被其他项目占用
     * @throws InvalidRequestException 日期区间不合法
     */
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findActive(id);
        String code = normalizeCode(request.code());
        if (repository.existsByCodeAndDeletedFalseAndIdNot(code, id)) {
            throw new DuplicateCodeException("项目编号已存在: " + code);
        }

        apply(project, request, code);
        return toResponse(repository.saveAndFlush(project));
    }

    /**
     * 软删除项目（置 {@code deleted = true}，数据保留）。重复删除返回 404。
     *
     * @throws NotFoundException 项目不存在或已删除
     */
    @Transactional
    public void delete(Long id) {
        Project project = findActive(id);
        project.setDeleted(true);
        repository.save(project);
    }

    private Project findActive(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("项目不存在: " + id));
    }

    private static void apply(Project project, ProjectRequest request, String code) {
        if (request.startDate() != null && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new InvalidRequestException("结束日期不能早于开始日期");
        }

        project.setCode(code);
        project.setName(request.name().trim());
        project.setDescription(trimToNull(request.description()));
        project.setStatus(request.status());
        project.setPriority(request.priority());
        project.setOwner(trimToNull(request.owner()));
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setProgress(request.progress() == null ? 0 : request.progress());
        project.setReceivableAmount(money(request.receivableAmount()));
        project.setPayableAmount(money(request.payableAmount()));
        project.setReceivedAmount(money(request.receivedAmount()));
        project.setPaidAmount(money(request.paidAmount()));
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

    private static BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private static String normalize(String keyword) {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }

    private static String normalizeCode(String code) {
        return code == null ? null : code.trim();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
