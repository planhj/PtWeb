package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.controller.Forum.dto.ForumCcommentDTO;
import com.example.ptweb.entity.ForumCcomment;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumSection;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.ForumCcommentMapper;
import com.example.ptweb.mapper.ForumCommentMapper;
import com.example.ptweb.mapper.ForumPostMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.ptweb.controller.Forum.dto.ForumCcommentDTO;

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

    public List<ForumCcommentDTO> getCcommentDTOsByCommentId(Long commentId) {
        List<ForumCcomment> ccomments = forumCcommentMapper.findBycommentId(commentId);
        return ccomments.stream().map(this::toDTO).toList();
    }

    public ForumCcommentDTO getCcommentDTOById(Long id) {
        ForumCcomment ccomment = forumCcommentMapper.selectById(id);
        return toDTO(ccomment);
    }

    // 创建评论
    public ForumCcommentDTO createCcomment(ForumCcomment ccomment, Long postId) {
        ccomment.setPostId(postId);  // 设置必填字段
        // ✅ 校验用户是否存在
        if (userMapper.selectById(ccomment.getUserId()) == null) {
            throw new IllegalArgumentException("用户不存在，无法评论");
        }

        // ✅ 校验评论是否存在
        if (forumCommentMapper.selectById(ccomment.getCommentId()) == null) {
            throw new IllegalArgumentException("评论不存在，无法评论");
        }


        // 设置创建时间和默认浏览数
        ccomment.setCreatedAt(LocalDateTime.now());
        if (ccomment.getView() == null) {
            ccomment.setView(0);
        }

        forumCcommentMapper.insert(ccomment);

        // 插入后数据库会自动填充 ID，再根据插入后的对象构造 DTO
        return toDTO(ccomment);
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

    private ForumCcommentDTO toDTO(ForumCcomment ccomment) {
        User user = userMapper.selectById(ccomment.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("评论用户不存在");
        }

        ForumCcommentDTO dto = new ForumCcommentDTO();
        dto.setId(ccomment.getId());
        dto.setCommentId(ccomment.getCommentId());
        dto.setUserId(ccomment.getUserId());
        dto.setContent(ccomment.getContent());
        dto.setView(ccomment.getView());
        dto.setCreatedAt(ccomment.getCreatedAt());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());

        return dto;
    }
}
