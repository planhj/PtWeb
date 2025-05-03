package com.example.ptweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("settings")
public class SettingEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    private String settingKey;

    private String settingValue;
}
