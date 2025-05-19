package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ptweb.type.PrivacyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private long id;

    private String email;

    @TableField("password")
    private String password;

    private String username;

    private String passkey;

    @TableField("create_at")
    private Timestamp createAt;

    private String avatar;

    @TableField("custom_title")
    private String customTitle;

    private String signature;

    @TableField("download_bandwidth")
    private String downloadBandwidth;

    @TableField("upload_bandwidth")
    private String uploadBandwidth;

    private long downloaded;

    private long uploaded;

    @TableField("real_downloaded")
    private long realDownloaded;

    @TableField("real_uploaded")
    private long realUploaded;

    private BigDecimal score;

    @TableField("invite_slot")
    private int inviteSlot;

    @TableField("seeding_time")
    private long seedingTime;

    @TableField("personal_access_token")
    private String personalAccessToken;

    @TableField("privacy_level")
    private PrivacyLevel privacyLevel;

    @TableField("last_sign_in_date")
    private Date lastSignInDate;

    @TableField("continuous_days")
    private Integer continuousDays;


}
