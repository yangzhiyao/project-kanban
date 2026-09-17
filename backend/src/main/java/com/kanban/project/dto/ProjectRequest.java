package com.kanban.project.dto;

import com.kanban.project.model.ProjectPriority;
import com.kanban.project.model.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新建（POST）与更新（PUT）项目的请求体。
 *
 * <p>{@code code}、{@code name}、{@code status}、{@code priority} 必填；
 * 其余可选，其中 {@code progress} 与四个金额为 {@code null} 时按 0 处理。
 * 校验失败返回 400，响应体的 {@code errors} 给出字段级错误信息。
 *
 * @param code             项目编号，必填，仅允许字母/数字/下划线/中划线，长度 2-32，全局唯一
 * @param name             项目名称，必填，最长 100
 * @param description      项目描述，可选，最长 1000
 * @param status           项目状态，必填，取值见 {@link ProjectStatus}
 * @param priority         优先级，必填，取值见 {@link ProjectPriority}
 * @param owner            负责人，可选，最长 50
 * @param startDate        计划开始日期，可选
 * @param endDate          计划结束日期，可选，不得早于 {@code startDate}
 * @param progress         进度百分比，可选，0-100
 * @param receivableAmount 应收金额，可选，非负，最多 13 位整数 + 2 位小数
 * @param payableAmount    应付金额，可选，非负，最多 13 位整数 + 2 位小数
 * @param receivedAmount   实收金额，可选，非负，最多 13 位整数 + 2 位小数
 * @param paidAmount       实付金额，可选，非负，最多 13 位整数 + 2 位小数
 */
public record ProjectRequest(
        @NotBlank(message = "项目编号不能为空")
        @Pattern(regexp = "[A-Za-z0-9_-]{2,32}", message = "项目编号只能包含字母、数字、- 和 _，长度 2-32")
        String code,

        @NotBlank(message = "项目名称不能为空")
        @Size(max = 100, message = "项目名称长度不能超过 100")
        String name,

        @Size(max = 1000, message = "项目描述长度不能超过 1000")
        String description,

        @NotNull(message = "项目状态不能为空")
        ProjectStatus status,

        @NotNull(message = "优先级不能为空")
        ProjectPriority priority,

        @Size(max = 50, message = "负责人长度不能超过 50")
        String owner,

        LocalDate startDate,

        LocalDate endDate,

        @Min(value = 0, message = "进度不能小于 0")
        @Max(value = 100, message = "进度不能大于 100")
        Integer progress,

        @DecimalMin(value = "0", message = "应收金额不能为负数")
        @Digits(integer = 13, fraction = 2, message = "应收金额最多 13 位整数、2 位小数")
        BigDecimal receivableAmount,

        @DecimalMin(value = "0", message = "应付金额不能为负数")
        @Digits(integer = 13, fraction = 2, message = "应付金额最多 13 位整数、2 位小数")
        BigDecimal payableAmount,

        @DecimalMin(value = "0", message = "实收金额不能为负数")
        @Digits(integer = 13, fraction = 2, message = "实收金额最多 13 位整数、2 位小数")
        BigDecimal receivedAmount,

        @DecimalMin(value = "0", message = "实付金额不能为负数")
        @Digits(integer = 13, fraction = 2, message = "实付金额最多 13 位整数、2 位小数")
        BigDecimal paidAmount) {
}
