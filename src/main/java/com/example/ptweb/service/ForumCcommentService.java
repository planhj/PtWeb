package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumCcomment;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.mapper.ForumCcommentMapper;
import com.example.ptweb.mapper.ForumCommentMapper;
import com.example.ptweb.mapper.ForumPostMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumCcommentService extends ServiceImpl<ForumCommentMapper, ForumComment> {

    private final ForumCommentMapper forumCommentMapper;
    private final ForumCcommentMapper forumCcommentMapper;
    private final ForumPostMapper forumPostMapper;
    private final UserMapper userMapper;
    // 获取某帖子的所有评论
    public List<ForumCcomment> getCcommentsByCommentId(Long commentId) {
        return forumCcommentMapper.findBycommentId(commentId);
    }

    public ForumCcomment getCcommentById(Long id) {
        return forumCcommentMapper.selectById(id);
    }

    // 创建评论
    public ForumCcomment createCcomment(ForumCcomment ccomment) {
        // ✅ 校验用户是否存在
        if (userMapper.selectById(ccomment.getUserId()) == null) {
            throw new IllegalArgumentException("用户不存在，无法评论");
        }

        // ✅ 校验版块是否存在
        if (forumCommentMapper.selectById(ccomment.getCommentId()) == null) {
            throw new IllegalArgumentException("评论不存在，无法评论");
        }

        // 发帖逻辑
        ccomment.setCreatedAt(LocalDateTime.now());
        ccomment.setUpdatedAt(LocalDateTime.now());
        if (ccomment.getViews() == null) {
            ccomment.setViews(0);
        }
        forumCcommentMapper.insert(ccomment);
        return ccomment;
    }

    // 更新评论
    public ForumCcomment updateCcomment(Long id, ForumCcomment ccomment) {
        ccomment.setId(id);
        forumCcommentMapper.updateById(ccomment);
        return ccomment;
    }

    // 删除评论
    public void deleteCcomment(Long id) {
        forumCcommentMapper.deleteById(id);
    }

}
