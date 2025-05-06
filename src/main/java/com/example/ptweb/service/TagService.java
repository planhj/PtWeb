package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.Tag;
import com.example.ptweb.mapper.TagMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    @Nullable
    public Tag getTag(@NotNull String tagName) {
        tagName = tagName.toLowerCase(Locale.ROOT);
        return tagMapper.selectOne(new QueryWrapper<Tag>().eq("name", tagName));
    }

    @Nullable
    public Tag getTag(long id) {
        return tagMapper.selectById(id);
    }

    @NotNull
    public Tag save(@NotNull Tag tag) {
        if (tag.getId() == 0) {
            tagMapper.insert(tag);
        } else {
            tagMapper.updateById(tag);
        }
        return tag;
    }

    @NotNull
    public List<Tag> getAllTags() {
        return tagMapper.selectList(null);
    }
}
