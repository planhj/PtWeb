package com.example.ptweb.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "item_categories")
public class item_categories {

    @TableId(value = "category_id", type = IdType.AUTO)
    private long categoryId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("display_order")
    private long displayOrder;

    @TableField("is_active")
    private boolean isActive;

    @TableField("price")
    private int price;

    public Boolean getIsActive() {
        return isActive;
    }

    public int getPrice(){
        return price;
    }


}
