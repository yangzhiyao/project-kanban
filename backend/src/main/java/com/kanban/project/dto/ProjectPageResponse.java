package com.kanban.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 项目分页响应，即 {@code GET /api/projects} 的返回体。
 *
 * @param items   当前页项目列表
 * @param total   满足筛选条件的总条数
 * @param page    当前页码，从 0 开始
 * @param size    每页条数
 * @param summary 满足筛选条件的全部数据的金额合计
 */
public record ProjectPageResponse(
        @Schema(description = "当前页项目列表") List<ProjectResponse> items,
        @Schema(description = "满足筛选条件的总条数", example = "8") long total,
        @Schema(description = "当前页码，从 0 开始", example = "0") int page,
        @Schema(description = "每页条数", example = "10") int size,
        @Schema(description = "满足筛选条件的全部数据的金额合计") ProjectSummary summary) {
}
