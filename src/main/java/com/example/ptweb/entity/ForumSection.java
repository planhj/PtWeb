package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_sections")
public class ForumSection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
