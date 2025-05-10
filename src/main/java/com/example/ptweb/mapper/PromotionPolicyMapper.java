package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.PromotionPolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PromotionPolicyMapper extends BaseMapper<PromotionPolicy> {

    @Select("SELECT * FROM promotion_policies WHERE slug = #{slug} LIMIT 1")
    PromotionPolicy findBySlug(String slug);
}