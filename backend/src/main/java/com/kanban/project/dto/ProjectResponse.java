package com.kanban.project.dto;

import com.kanban.project.model.ProjectPriority;
import com.kanban.project.model.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * 项目响应体。
 *
 * <p>金额均为 2 位小数的 {@link BigDecimal}，序列化后是 JSON 数字。
 *
 * <p>利润字段不落库，由后端派生返回：
 * <ul>
 *   <li>{@code expectedProfit} = {@code receivableAmount} - {@code payableAmount}（预计利润）</li>
 *   <li>{@code actualProfit} = {@code receivedAmount} - {@code paidAmount}（实际利润）</li>
 * </ul>
 *
 * @param id               主键
 * @param code             项目编号，全局唯一
 * @param name             项目名称
 * @param description      项目描述
 * @param status           项目状态
 * @param priority         优先级
 * @param owner            负责人
 * @param startDate        计划开始日期
 * @param endDate          计划结束日期
 * @param progress         进度百分比（0-100）
 * @param receivableAmount 应收金额
 * @param payableAmount    应付金额
 * @param receivedAmount   实收金额
 * @param paidAmount       实付金额
 * @param expectedProfit   预计利润（派生）
 * @param actualProfit     实际利润（派生）
 * @param createdAt        创建时间（UTC）
 * @param updatedAt        最后更新时间（UTC）
 */
public record ProjectResponse(
        @Schema(description = "主键", example = "1") Long id,
        @Schema(description = "项目编号，全局唯一", example = "PRJ-0001") String code,
        @Schema(description = "项目名称", example = "企业门户网站重构") String name,
        @Schema(description = "项目描述", example = "统一门户与内容管理平台重构", nullable = true) String description,
        @Schema(description = "项目状态", example = "IN_PROGRESS") ProjectStatus status,
        @Schema(description = "优先级", example = "HIGH") ProjectPriority priority,
        @Schema(description = "负责人", example = "张三", nullable = true) String owner,
        @Schema(description = "计划开始日期", example = "2026-01-06", nullable = true) LocalDate startDate,
        @Schema(description = "计划结束日期", example = "2026-06-30", nullable = true) LocalDate endDate,
        @Schema(description = "进度百分比（0-100）", example = "45") Integer progress,
        @Schema(description = "应收金额", example = "1200000.00") BigDecimal receivableAmount,
        @Schema(description = "应付金额", example = "700000.00") BigDecimal payableAmount,
        @Schema(description = "实收金额", example = "500000.00") BigDecimal receivedAmount,
        @Schema(description = "实付金额", example = "300000.00") BigDecimal paidAmount,
        @Schema(description = "预计利润（派生）= 应收 - 应付", example = "500000.00") BigDecimal expectedProfit,
        @Schema(description = "实际利润（派生）= 实收 - 实付", example = "200000.00") BigDecimal actualProfit,
        @Schema(description = "创建时间（UTC）", example = "2026-01-05T01:00:00Z") Instant createdAt,
        @Schema(description = "最后更新时间（UTC）", example = "2026-03-01T01:00:00Z") Instant updatedAt) {
}
