package com.example.ptweb.controller.Forum;



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

@RestController
@RequestMapping("/api/forum/likes")
@RequiredArgsConstructor
public class ForumPostLikeController {

    private final ForumPostLikeService likeService;

    @PostMapping("/like")
    public String likePost(@RequestParam Long postId, @RequestParam Long userId) {
        likeService.likePost(postId, userId);
        return "点赞成功";
    }

    @PostMapping("/unlike")
    public String unlikePost(@RequestParam Long postId, @RequestParam Long userId) {
        likeService.unlikePost(postId, userId);
        return "取消点赞成功";
    }

    @GetMapping("/count")
    public long getLikeCount(@RequestParam Long postId) {
        return likeService.getLikeCount(postId);
    }

    @GetMapping("/has-liked")
    public boolean hasLiked(@RequestParam Long postId, @RequestParam Long userId) {
        return likeService.hasUserLiked(postId, userId);
    }
}
