package com.example.ptweb.controller.item;

import org.jetbrains.annotations.NotNull;

@Data
static class ItemStatusUpdateDTO {
    @NotNull(message = "状态不能为空")
    private Boolean isActive;
}
