package com.kanban.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProjectSummary(
        BigDecimal receivableAmount,
        BigDecimal payableAmount,
        BigDecimal receivedAmount,
        BigDecimal paidAmount) {

    public ProjectSummary {
        receivableAmount = zeroIfNull(receivableAmount);
        payableAmount = zeroIfNull(payableAmount);
        receivedAmount = zeroIfNull(receivedAmount);
        paidAmount = zeroIfNull(paidAmount);
    }

    public static ProjectSummary empty() {
        return new ProjectSummary(null, null, null, null);
    }

    @JsonProperty("expectedProfit")
    public BigDecimal expectedProfit() {
        return receivableAmount.subtract(payableAmount);
    }

    @JsonProperty("actualProfit")
    public BigDecimal actualProfit() {
        return receivedAmount.subtract(paidAmount);
    }

    private static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
