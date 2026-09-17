package com.kanban.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * 健康检查响应体。
 *
 * @param status      服务状态，正常时为 {@code UP}
 * @param application 应用名（{@code spring.application.name}）
 * @param timestamp   服务器当前时间（UTC）
 */
public record HealthResponse(
        @Schema(description = "服务状态，正常时为 UP", example = "UP") String status,
        @Schema(description = "应用名", example = "kanban-project") String application,
        @Schema(description = "服务器当前时间（UTC）", example = "2026-09-17T04:29:06.242896Z") Instant timestamp) {
}
