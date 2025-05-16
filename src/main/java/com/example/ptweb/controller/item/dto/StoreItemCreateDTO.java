package com.example.ptweb.controller.item;

import org.jetbrains.annotations.NotNull;
import com.example.ptweb.entity.item_categories.java;
import com.example.ptweb.mapper.item_categoriesMapper.java;
import com.example.ptweb.service.itemService.java;


//------------------------ 相关DTO定义 ------------------------

@Data
static class StoreItemCreateDTO {
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

