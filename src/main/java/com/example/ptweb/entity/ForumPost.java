package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_posts")
public class ForumPost {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("section_id")
    private Long sectionId;

    @TableField("user_id")
    private Long userId;

    private String title;

    private String content;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    private Integer views;
}
