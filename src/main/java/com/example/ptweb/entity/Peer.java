package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("peers")
public class Peer {

    @TableId
    private long id;

    private String ip;
    private int port;
    private String infoHash;
    private String peerId;
    private String userAgent;
    private long uploaded;
    private long downloaded;

    @TableField("to_go")
    private long left;

    private boolean seeder;
    private boolean partialSeeder;
    private String passKey;

    @TableField("update_at")
    private Timestamp updateAt;

    private long seedingTime;
    private long uploadSpeed;
    private long downloadSpeed;

    @TableField(exist = false)
    private User user; // 不作为数据库字段
}
