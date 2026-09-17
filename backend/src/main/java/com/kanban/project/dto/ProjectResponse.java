package com.kanban.project.dto;

import com.kanban.project.model.ProjectPriority;
import com.kanban.project.model.ProjectStatus;

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
        Long id,
        String code,
        String name,
        String description,
        ProjectStatus status,
        ProjectPriority priority,
        String owner,
        LocalDate startDate,
        LocalDate endDate,
        Integer progress,
        BigDecimal receivableAmount,
        BigDecimal payableAmount,
        BigDecimal receivedAmount,
        BigDecimal paidAmount,
        BigDecimal expectedProfit,
        BigDecimal actualProfit,
        Instant createdAt,
        Instant updatedAt) {
}
