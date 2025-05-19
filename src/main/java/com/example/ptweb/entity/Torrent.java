package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "torrents", autoResultMap = true)
public class Torrent {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("info_hash")
    private String infoHash;

    @TableField("user_id")
    private Long userId;

    private String title;

    @TableField("sub_title")
    private String subTitle;

    private long size;

    @TableField("created_at")
    private Timestamp createdAt;

    @TableField("updated_at")
    private Timestamp updatedAt;

    @TableField("under_review")
    private boolean underReview;

    private boolean anonymous;

    @TableField("category_id")
    private Long categoryId;

    @TableField("promotion_policy_id")
    private Long promotionPolicyId;

    @TableField("description")
    private String description;

    @TableField(value = "tag", typeHandler = JacksonTypeHandler.class)
    private List<Long> tag;  // 存储多个 Tag 的 ID
    private int completedCount;
    private int leecherCount;
    private int seederCount;

    public String getUsernameWithAnonymous(String username, boolean canSeeAnonymous) {
        return canSeeAnonymous || !anonymous ? username : "Anonymous";
    }
}
