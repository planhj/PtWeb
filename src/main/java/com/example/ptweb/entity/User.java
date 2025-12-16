package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ptweb.type.CustomTitle;
import lombok.*;
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
    private CustomTitle customTitle;

    private String signature;

    private long downloaded;

    private long uploaded;

    @TableField("real_downloaded")
    private long realDownloaded;

    @TableField("real_uploaded")
    private long realUploaded;

    private BigDecimal score;

    @TableField("seeding_time")
    private long seedingTime;

    @TableField("personal_access_token")
    private String personalAccessToken;

    @TableField("last_sign_in_date")
    private Date lastSignInDate;

    @TableField("continuous_days")
    private Integer continuousDays;
    @Setter
    @Getter
    private String status;
    public double getDownloadRatio() {
        return customTitle != null ? customTitle.getDownloadRatio() : 1;
    }
    public double getUploadedRatio() {
        return customTitle != null ? customTitle.getUploadRatio() : 1;
    }

}
