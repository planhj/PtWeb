package com.example.ptweb.controller.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreItemCreateDTO {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    private Integer stock;
    private String description;
    private Boolean isFeatured;
}


