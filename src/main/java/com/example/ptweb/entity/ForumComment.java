package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_comments")
public class ForumComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("user_id")
    private Long userId;

    private String content;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    private Integer views;
}
