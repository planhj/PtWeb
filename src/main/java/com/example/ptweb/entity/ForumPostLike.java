package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_post_likes")
public class ForumPostLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("user_id")
    private Long userId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
