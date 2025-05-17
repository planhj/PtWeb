package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.mapper.ForumCommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumCommentService extends ServiceImpl<ForumCommentMapper, ForumComment> {

    private final ForumCommentMapper forumCommentMapper;

    public ForumCommentService(ForumCommentMapper forumCommentMapper) {
        this.forumCommentMapper = forumCommentMapper;
    }

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
