package com.example.ptweb.controller.Forum;



import com.example.ptweb.entity.ForumComment;
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
@RequestMapping("/forum/comments")
@RequiredArgsConstructor
public class ForumCommentController {

    private final ForumCommentService commentService;

    @GetMapping("/post/{postId}")
    public List<ForumComment> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping("/post/{post_id}/{user_id}")
    public ForumComment createComment(@PathVariable("post_id") Long postId,
                                      @PathVariable("user_id") Long userId,
                                       @RequestBody ForumComment comment) {
        comment.setPostId(postId);
        comment.setUserId(userId);
        return commentService.createComment(comment);
    }

    @PutMapping("/{id}")
    public ForumComment updateComment(@PathVariable Long id, @RequestBody ForumComment comment) {
        return commentService.updateComment(id, comment);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
