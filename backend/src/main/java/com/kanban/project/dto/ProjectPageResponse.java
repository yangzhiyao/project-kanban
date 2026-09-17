package com.kanban.project.dto;

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
        List<ProjectResponse> items,
        long total,
        int page,
        int size,
        ProjectSummary summary) {
}
