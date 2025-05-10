package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.Category;
import com.example.ptweb.mapper.CategoryMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Nullable
    public Category getCategory(@NotNull String slug) {
        return categoryMapper.selectOne(new QueryWrapper<Category>().eq("slug", slug));
    }

    @Nullable
    public Category getCategory(long id) {
        return categoryMapper.selectById(id);
    }

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null); // 查询全部
    }

    @NotNull
    public Category save(@NotNull Category category) {
        if (category.getId() == 0) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return category;
    }
}