package com.example.ptweb.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "categories")
public class Category {

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("slug")
    private String slug;

    @TableField("name")
    private String name;

    @TableField("icon")
    private String icon;
}
