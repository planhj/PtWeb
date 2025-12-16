package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_Ccomments")
public class ForumCcomment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("comment_id")
    private Long commentId;

    @TableField("user_id")
    private Long userId;

    private String content;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("post_id")
    private Long PostId;


    private Integer view;
}
