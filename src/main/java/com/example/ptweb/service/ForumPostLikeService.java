package com.example.ptweb.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ptweb.entity.ForumPostLike;
import com.example.ptweb.mapper.ForumPostLikeMapper;
import org.springframework.stereotype.Service;

@Service
public class ForumPostLikeService extends ServiceImpl<ForumPostLikeMapper, ForumPostLike> {

    public boolean hasUserLikedPost(Long userId, Long postId) {
        return this.lambdaQuery()
                .eq(ForumPostLike::getUserId, userId)
                .eq(ForumPostLike::getPostId, postId)
                .count() > 0;
    }

    public void likePost(Long userId, Long postId) {
        if (!hasUserLikedPost(userId, postId)) {
            ForumPostLike like = new ForumPostLike();
            like.setUserId(userId);
            like.setPostId(postId);
            like.setCreatedAt(java.time.LocalDateTime.now());
            this.save(like);
        }
    }

    public void unlikePost(Long userId, Long postId) {
        this.lambdaUpdate()
                .eq(ForumPostLike::getUserId, userId)
                .eq(ForumPostLike::getPostId, postId)
                .remove();
    }
    // 获取帖子点赞数
    public Long getLikeCount(Long postId) {
        return this.lambdaQuery()
                .eq(ForumPostLike::getPostId, postId)
                .count();
    }

    // 判断用户是否已点赞该帖子
    public boolean hasUserLiked(Long postId, Long userId) {
        return this.lambdaQuery()
                .eq(ForumPostLike::getPostId, postId)
                .eq(ForumPostLike::getUserId, userId)
                .count() > 0;
    }
}
