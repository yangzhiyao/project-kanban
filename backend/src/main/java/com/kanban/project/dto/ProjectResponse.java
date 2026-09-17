package com.kanban.project.dto;

import com.kanban.project.model.ProjectPriority;
import com.kanban.project.model.ProjectStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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
