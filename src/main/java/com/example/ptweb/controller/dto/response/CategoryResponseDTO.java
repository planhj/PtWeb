package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.Category;
import com.example.ptweb.other.ResponsePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class CategoryResponseDTO extends ResponsePojo {
    private long id;
    private String slug;
    private String name;
    private String icon;

    public CategoryResponseDTO(@NotNull Category category) {
        this.id = category.getId();
        this.slug = category.getSlug();
        this.name = category.getName();
        this.icon = category.getIcon();
    }
}
