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
@TableName("promotion_policies")
public class PromotionPolicy {

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("slug")
    private String slug;

    @TableField("display_name")
    private String displayName;

    @TableField("upload_ratio")
    private double uploadRatio;

    @TableField("download_ratio")
    private double downloadRatio;

    public double applyUploadRatio(double upload) {
        return upload * uploadRatio;
    }

    public double applyDownloadRatio(double download) {
        return download * downloadRatio;
    }
}