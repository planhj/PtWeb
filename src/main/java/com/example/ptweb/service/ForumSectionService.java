package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.mapper.ForumSectionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumSectionService extends ServiceImpl<ForumSectionMapper, ForumSection> {
    // 可以扩展板块的自定义业务逻辑
    private final ForumSectionMapper forumSectionMapper = null;

    public List<ForumSection> getAllSections() {
        return forumSectionMapper.selectList(null);
    }

    public ForumSection createCategory(ForumSection section) {
        forumSectionMapper.insert(section);
        // 插入后，section的id会被自动赋值（前提是MyBatis-Plus配置正确）
        return section;
    }

    public ForumSection updateCategory(Long id, ForumSection category) {
        // 先根据 id 查询数据库中是否存在该分类
        ForumSection existing = forumSectionMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("分类不存在，id=" + id);
        }

        // 把传入的 category 的字段更新到 existing 中，id 保持不变
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        // 如果有其他字段，也一起更新...

        // 执行更新操作
        int rows = forumSectionMapper.updateById(existing);
        if (rows == 0) {
            throw new RuntimeException("更新失败，id=" + id);
        }

        // 返回更新后的对象
        return existing;
    }

    public void deleteCategory(Long id) {
        // 先判断分类是否存在
        ForumSection existing = forumSectionMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("分类不存在，id=" + id);
        }

        // 执行删除操作
        int rows = forumSectionMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("删除失败，id=" + id);
        }
    }
}