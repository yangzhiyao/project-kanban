package com.kanban.project.controller;

import com.kanban.project.dto.ProjectPageResponse;
import com.kanban.project.dto.ProjectRequest;
import com.kanban.project.dto.ProjectResponse;
import com.kanban.project.exception.DuplicateCodeException;
import com.kanban.project.exception.InvalidRequestException;
import com.kanban.project.exception.NotFoundException;
import com.kanban.project.model.ProjectStatus;
import com.kanban.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目 REST 接口，统一前缀 {@code /api/projects}，请求与响应均为 UTF-8 JSON。
 *
 * <p>错误响应统一为 RFC 7807 {@code ProblemDetail}，结构
 * {@code {"status":400,"title":"...","detail":"...","instance":"/api/projects","errors":{...}}}，
 * 其中 {@code errors} 仅在字段校验失败时出现。错误码约定：
 * <ul>
 *   <li>400 参数校验失败 / 日期区间不合法 / 枚举值无效</li>
 *   <li>404 项目不存在或已软删除</li>
 *   <li>409 项目编号重复</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * {@code GET /api/projects} — 分页查询项目列表（已排除软删除数据），按更新时间倒序。
     *
     * @param keyword 可选，忽略大小写模糊匹配项目编号或名称
     * @param status  可选，按项目状态精确过滤，取值 {@code PLANNING|IN_PROGRESS|ON_HOLD|COMPLETED|CANCELLED}
     * @param page    页码，从 0 开始，默认 0
     * @param size    每页条数，默认 10，最大 100
     * @return 分页数据与当前筛选条件下的金额合计
     */
    @GetMapping
    public ProjectPageResponse list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) ProjectStatus status,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return projectService.list(keyword, status, page, size);
    }

    /**
     * {@code GET /api/projects/{id}} — 查询项目详情。
     *
     * @param id 项目主键
     * @return 项目详情（含派生利润字段）
     * @throws NotFoundException 项目不存在或已软删除（404）
     */
    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id) {
        return projectService.get(id);
    }

    /**
     * {@code POST /api/projects} — 新建项目。
     *
     * @param request 项目数据，字段约束见 {@link com.kanban.project.dto.ProjectRequest}
     * @return 创建后的项目，HTTP 201
     * @throws DuplicateCodeException 项目编号已存在（409）
     * @throws InvalidRequestException 日期区间不合法（400）
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        return projectService.create(request);
    }

    /**
     * {@code PUT /api/projects/{id}} — 更新项目（全量覆盖，未传的可选字段回落到默认值）。
     *
     * @param id      项目主键
     * @param request 项目数据，字段约束见 {@link com.kanban.project.dto.ProjectRequest}
     * @return 更新后的项目
     * @throws NotFoundException 项目不存在或已软删除（404）
     * @throws DuplicateCodeException 编号被其他项目占用（409）
     * @throws InvalidRequestException 日期区间不合法（400）
     */
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    /**
     * {@code DELETE /api/projects/{id}} — 删除项目（软删除，数据保留）。
     *
     * @param id 项目主键
     * @throws NotFoundException 项目不存在或已软删除（404）
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}
