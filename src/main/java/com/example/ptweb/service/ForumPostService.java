package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumPost;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.mapper.ForumPostMapper;
import com.example.ptweb.mapper.ForumSectionMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumPostService extends ServiceImpl<ForumPostMapper, ForumPost> {

    private final ForumPostMapper forumPostMapper;
    private final UserMapper userMapper;
    private final ForumSectionMapper forumSectionMapper;

    public List<ForumPost> getAllPosts(long section_id) {
        return forumPostMapper.selectAllposts(section_id);
        //return list();
    }

    public ForumPost getPostById(Long id) {
        return getById(id);
    }

    public ForumPost createPost(ForumPost post) {
        // ✅ 校验用户是否存在
        if (userMapper.selectById(post.getUserId()) == null) {
            throw new IllegalArgumentException("用户不存在，无法发帖");
        }

        // ✅ 校验版块是否存在
        if (forumSectionMapper.selectById(post.getSectionId()) == null) {
            throw new IllegalArgumentException("版块不存在，无法发帖");
        }

        // 发帖逻辑
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        if (post.getViews() == null) {
            post.setViews(0);
        }
        forumPostMapper.insert(post);
        return post;
    }




    public ForumPost updatePost(Long id, ForumPost post) {
        post.setId(id);
        updateById(post);
        return post;
    }

    public void deletePost(Long id) {
        removeById(id);
    }

    public void incrementViews(Long postId) {
        forumPostMapper.incrementViews(postId);
    }

    public List<ForumPost> getPostsBySectionId(Long sectionId) {
        return this.lambdaQuery()
                .eq(ForumPost::getSectionId, sectionId)
                .orderByDesc(ForumPost::getCreatedAt)
                .list();
    }
}
