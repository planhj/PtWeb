package com.example.ptweb.entity;

import lombok.Getter;

@Getter
public enum CustomTitle {
    NORMAL(1.0, 1.0, "普通用户"),
    VIP(1.5, 1.0, "VIP"),
    SVIP(2.0, 0.8, "SVIP");

    private final double uploadRatio;
    private final double downloadRatio;
    private final String description;

    CustomTitle(double uploadRatio, double downloadRatio, String description) {
        this.uploadRatio = uploadRatio;
        this.downloadRatio = downloadRatio;
        this.description = description;
    }
}