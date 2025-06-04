package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumPost;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.ForumPostMapper;
import com.example.ptweb.mapper.ForumSectionMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.ptweb.controller.Forum.dto.ForumPostDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public ForumPostDTO createPost(ForumPost post) {
        User user = userMapper.selectById(post.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，无法发帖");
        }

        if (forumSectionMapper.selectById(post.getSectionId()) == null) {
            throw new IllegalArgumentException("版块不存在，无法发帖");
        }

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        if (post.getView() == null) {
            post.setView(0);
        }
        forumPostMapper.insert(post);

        ForumPostDTO dto = new ForumPostDTO();
        dto.setId(post.getId());
        dto.setSectionId(post.getSectionId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setView(post.getView());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());

        return dto;
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

    // 新增：返回带用户名和头像的帖子列表
    public List<ForumPostDTO> getPostsWithUserInfoBySection(Long sectionId) {
        List<ForumPost> posts = forumPostMapper.selectAllposts(sectionId);
        List<ForumPostDTO> dtos = new ArrayList<>();

        for (ForumPost post : posts) {
            var user = userMapper.selectById(post.getUserId());
            if (user == null) {
                continue; // 用户不存在就跳过
            }

            ForumPostDTO dto = new ForumPostDTO();
            dto.setId(post.getId());
            dto.setSectionId(post.getSectionId());
            dto.setUserId(post.getUserId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setUpdatedAt(post.getUpdatedAt());
            dto.setView(post.getView());
            dto.setUsername(user.getUsername());
            dto.setAvatar(user.getAvatar());

            dtos.add(dto);
        }

        return dtos;
    }
}
