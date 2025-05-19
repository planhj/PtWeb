package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.mapper.ForumCommentMapper;
import com.example.ptweb.mapper.ForumPostMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 获取某一条评论
    public ForumComment getCommentById(Long id) {
        return forumCommentMapper.selectById(id);
    }

    // 创建评论
    public ForumComment createComment(ForumComment comment) {
        // ✅ 校验用户是否存在
        if (userMapper.selectById(comment.getUserId()) == null) {
            throw new IllegalArgumentException("用户不存在，无法评论");
        }

        // ✅ 校验版块是否存在
        if (forumPostMapper.selectById(comment.getPostId()) == null) {
            throw new IllegalArgumentException("帖子不存在，无法评论");
        }

        // 发帖逻辑
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        if (comment.getViews() == null) {
            comment.setViews(0);
        }
        forumCommentMapper.insert(comment);
        return comment;
    }

    // 更新评论
    public ForumComment updateComment(Long id, ForumComment comment) {
        comment.setId(id);
        forumCommentMapper.updateById(comment);
        return comment;
    }

    // 删除评论
    public void deleteComment(Long id) {
        forumCommentMapper.deleteById(id);
    }

}
