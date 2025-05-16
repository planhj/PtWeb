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
@TableName(value = "item_categories")
public class Item_Category {

    @TableId(value = "category_id", type = IdType.AUTO)
    private long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("display_order")
    private long order;

    @TableField("is_active")
    private boolean is_active;

}
