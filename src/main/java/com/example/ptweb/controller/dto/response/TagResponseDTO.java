package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.Tag;
import com.example.ptweb.other.ResponsePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class TagResponseDTO extends ResponsePojo{
    private long id;
    private String name;
    public TagResponseDTO(@NotNull Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
