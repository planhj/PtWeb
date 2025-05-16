package com.example.ptweb.controller.item;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

@Data
static class PointsAdjustDTO {
    @NotBlank(message = "操作类型不能为空")
    @Pattern(regexp = "ADD|DEDUCT", message = "无效的操作类型")
    private String operation;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    private String remark;
}
