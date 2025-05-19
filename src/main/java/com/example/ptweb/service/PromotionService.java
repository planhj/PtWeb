package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.PromotionPolicy;
import com.example.ptweb.mapper.PromotionPolicyMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionService {

    @Autowired
    private PromotionPolicyMapper mapper;

    @Nullable
    public PromotionPolicy getPromotionPolicy(long id) {
        return mapper.selectById(id);
    }

    @Nullable
    public PromotionPolicy getPromotionPolicy(@NotNull String name) {
        return mapper.selectOne(new QueryWrapper<PromotionPolicy>().eq("slug", name));
    }

    @Nullable
    public PromotionPolicy getDefaultPromotionPolicy() {
        List<PromotionPolicy> all = mapper.selectList(null);
        return all.isEmpty() ? null : all.get(2);
    }

    @NotNull
    public List<PromotionPolicy> getAllPromotionPolicies() {
        return mapper.selectList(null);
    }

    @NotNull
    public PromotionPolicy save(@NotNull PromotionPolicy promotionPolicy) {
        if (promotionPolicy.getId() == 0) {
            mapper.insert(promotionPolicy);
        } else {
            mapper.updateById(promotionPolicy);
        }
        return promotionPolicy;
    }
}
