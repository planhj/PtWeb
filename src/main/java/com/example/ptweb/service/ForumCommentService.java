package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.mapper.ForumCommentMapper;
import com.example.ptweb.mapper.ForumPostMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.ptweb.controller.Forum.dto.ForumCommentDTO;
import com.example.ptweb.entity.User;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumCommentService extends ServiceImpl<ForumCommentMapper, ForumComment> {

    private final ForumCommentMapper forumCommentMapper;
    private final ForumPostMapper forumPostMapper;
    private final UserMapper userMapper;
    // 获取某帖子的所有评论
    public List<ForumComment> getCommentsByPostId(Long postId) {
        return forumCommentMapper.findByPostId(postId);
    }

    public List<ForumCommentDTO> getCommentDTOsByPostId(Long postId) {
        List<ForumComment> comments = forumCommentMapper.findByPostId(postId);
        return comments.stream().map(this::toDTO).toList();
    }

    // 获取某一条评论
    public ForumComment getCommentById(Long id) {
        return forumCommentMapper.selectById(id);
    }

    // 创建评论
    // ✅ 创建评论并返回 DTO
    public ForumCommentDTO createComment(ForumComment comment) {
        if (userMapper.selectById(comment.getUserId()) == null) {
            throw new IllegalArgumentException("用户不存在，无法评论");
        }

        if (forumPostMapper.selectById(comment.getPostId()) == null) {
            throw new IllegalArgumentException("帖子不存在，无法评论");
        }

        comment.setCreatedAt(LocalDateTime.now());

        if (comment.getView() == null) {
            comment.setView(0);
        }

        forumCommentMapper.insert(comment);
        return toDTO(comment);
    }

    public ForumCommentDTO updateComment(Long id, ForumComment comment) {
        comment.setId(id);
        forumCommentMapper.updateById(comment);
        return toDTO(comment);
    }

    // 删除评论
    public void deleteComment(Long id) {
        forumCommentMapper.deleteById(id);
    }

    private ForumCommentDTO toDTO(ForumComment comment) {
        User user = userMapper.selectById(comment.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("评论用户不存在");
        }

        ForumCommentDTO dto = new ForumCommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setView(comment.getView());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());

        return dto;
    }


}
