package com.kanban.project.controller;

import com.kanban.project.dto.ProjectPageResponse;
import com.kanban.project.dto.ProjectRequest;
import com.kanban.project.dto.ProjectResponse;
import com.kanban.project.exception.DuplicateCodeException;
import com.kanban.project.exception.InvalidRequestException;
import com.kanban.project.exception.NotFoundException;
import com.kanban.project.model.ProjectStatus;
import com.kanban.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "项目", description = "项目 CRUD。删除为软删除（deleted=true），列表与详情均不再返回已删除数据。")
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
    @Operation(summary = "分页查询项目列表",
            description = "按更新时间倒序返回，并给出当前筛选条件下全部数据的金额合计（不是当前页合计）。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "分页结果与合计",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "status 枚举值非法", content = @Content)
    })
    @GetMapping
    public ProjectPageResponse list(
            @Parameter(description = "模糊匹配项目编号或名称（忽略大小写）", example = "中台")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "按状态精确过滤")
            @RequestParam(required = false) ProjectStatus status,
            @Parameter(description = "页码，从 0 开始", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页条数，最大 100", example = "10")
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
    @Operation(summary = "查询项目详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "项目详情",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "项目不存在或已删除", content = @Content)
    })
    @GetMapping("/{id}")
    public ProjectResponse get(
            @Parameter(description = "项目主键", example = "1") @PathVariable Long id) {
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
    @Operation(summary = "新建项目", description = "编号重复返回 409；日期区间不合法返回 400。")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功，返回创建后的项目",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "字段校验失败或日期区间不合法", content = @Content),
            @ApiResponse(responseCode = "409", description = "项目编号已存在", content = @Content)
    })
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
    @Operation(summary = "更新项目（全量覆盖）",
            description = "未提交的可选字段会回落到默认值；编号可修改，但不得与其他项目重复。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "字段校验失败或日期区间不合法", content = @Content),
            @ApiResponse(responseCode = "404", description = "项目不存在或已删除", content = @Content),
            @ApiResponse(responseCode = "409", description = "项目编号被其他项目占用", content = @Content)
    })
    @PutMapping("/{id}")
    public ProjectResponse update(
            @Parameter(description = "项目主键", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return projectService.update(id, request);
    }

    /**
     * {@code DELETE /api/projects/{id}} — 删除项目（软删除，数据保留）。
     *
     * @param id 项目主键
     * @throws NotFoundException 项目不存在或已软删除（404）
     */
    @Operation(summary = "删除项目（软删除）", description = "仅置 deleted=true，数据保留；重复删除返回 404。")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功", content = @Content),
            @ApiResponse(responseCode = "404", description = "项目不存在或已删除", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "项目主键", example = "1") @PathVariable Long id) {
        projectService.delete(id);
    }
}
