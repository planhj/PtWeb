package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("invite_code")
public class InviteCode {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("used")
    private Boolean used;

    @TableField("used_by")
    private Long usedBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("used_time")
    private LocalDateTime usedTime;
}