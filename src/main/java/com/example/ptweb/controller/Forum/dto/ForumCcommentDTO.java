package com.example.ptweb.controller.Forum.dto;





import com.example.ptweb.entity.ForumSection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ptweb.service.ForumSectionService;
import com.example.ptweb.service.ForumPostService;
import com.example.ptweb.service.ForumPostLikeService;
import com.example.ptweb.service.ForumCommentService;
import com.example.ptweb.entity.item_categories;
import com.example.ptweb.service.itemService;
import com.example.ptweb.service.itemService.PurchaseResult;
import com.example.ptweb.service.itemService.BusinessException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.time.LocalDateTime;

public class ForumCcommentDTO {
    private Long id;
    private Long PostId;
    private Long userId;
    private long CommentId;
    private String content;
    private LocalDateTime createdAt;
    private Integer view;

    private String username;   // 用户名
    private String avatar;     // 用户头像

    // getters 和 setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return PostId;
    }

    public void setPostId(Long PostId) {
        this.PostId = PostId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommentId() {
        return CommentId;
    }

    public void setCommentId(Long CommentId) {
        this.CommentId = CommentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
