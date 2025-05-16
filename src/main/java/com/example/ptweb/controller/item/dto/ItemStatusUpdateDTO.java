package com.example.ptweb.controller.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemStatusUpdateDTO {
    @NotNull(message = "状态不能为空")
    private Boolean isActive;
}
