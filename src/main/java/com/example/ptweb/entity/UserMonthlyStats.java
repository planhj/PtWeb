package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_monthly_stats")
public class UserMonthlyStats {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String month; // 格式如 "2024-06"

    private Long uploaded; // 本月上传字节数

    private Long seedingTime; // 本月做种秒数
}