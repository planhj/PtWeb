package com.example.ptweb.controller.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ExchangeRequest {

    @NotNull(message = "商品ID不能为空")
    @Min(value = 1, message = "无效的商品ID")
    private Integer itemId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
}
