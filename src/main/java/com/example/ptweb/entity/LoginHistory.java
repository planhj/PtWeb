package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ptweb.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("login_history")
public class LoginHistory {
    @TableId
    private long id;

    private Long userId;
    private Timestamp loginTime;
    private LoginType loginType;
    private String ipAddress;
    private String userAgent;
}
