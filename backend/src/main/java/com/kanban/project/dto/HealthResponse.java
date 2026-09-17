package com.kanban.project.dto;

import java.time.Instant;

/**
 * 健康检查响应体。
 *
 * @param status      服务状态，正常时为 {@code UP}
 * @param application 应用名（{@code spring.application.name}）
 * @param timestamp   服务器当前时间（UTC）
 */
public record HealthResponse(String status, String application, Instant timestamp) {
}
