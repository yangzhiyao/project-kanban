package com.kanban.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 列表金额合计。
 *
 * <p>统计范围是<b>当前筛选条件下的全部数据</b>（不是当前页），供列表底部合计行使用。
 * 无匹配数据时各字段为 0。
 *
 * <p>序列化后额外包含两个派生字段：
 * {@code expectedProfit}（预计利润 = 应收 - 应付）与 {@code actualProfit}（实际利润 = 实收 - 实付）。
 *
 * @param receivableAmount 应收合计
 * @param payableAmount    应付合计
 * @param receivedAmount   实收合计
 * @param paidAmount       实付合计
 */
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

    /** 预计利润 = 应收合计 - 应付合计。 */
    @JsonProperty("expectedProfit")
    public BigDecimal expectedProfit() {
        return receivableAmount.subtract(payableAmount);
    }

    /** 实际利润 = 实收合计 - 实付合计。 */
    @JsonProperty("actualProfit")
    public BigDecimal actualProfit() {
        return receivedAmount.subtract(paidAmount);
    }

    private static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
