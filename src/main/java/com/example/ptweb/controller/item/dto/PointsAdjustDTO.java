package com.example.ptweb.controller.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointsAdjustDTO {

    @NotBlank(message = "操作类型不能为空")
    @Pattern(regexp = "ADD|DEDUCT", message = "无效的操作类型")
    private String operation;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    private String remark;
}
