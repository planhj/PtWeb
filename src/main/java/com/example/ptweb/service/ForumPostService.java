package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumPost;
import com.example.ptweb.mapper.ForumPostMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumPostService extends ServiceImpl<ForumPostMapper, ForumPost> {

    private final ForumPostMapper forumPostMapper;

    public ForumPostService(ForumPostMapper forumPostMapper) {
        this.forumPostMapper = forumPostMapper;
    }

    public List<ForumPost> getAllPosts() {
        return list();
    }

    public ForumPost getPostById(Long id) {
        return getById(id);
    }

    public ForumPost createPost(ForumPost post) {
        save(post);
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
