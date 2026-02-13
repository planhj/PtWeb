package com.example.ptweb.controller.Forum;



import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.controller.Forum.dto.ForumCommentDTO;
import com.example.ptweb.entity.ForumComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ForumCommentController {

    private final ForumCommentService commentService;


    @GetMapping("/post/{postId}")
    public List<ForumCommentDTO> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentDTOsByPostId(postId);
    }


    @PostMapping("/post/{post_id}")
    public ForumCommentDTO createComment(@PathVariable("post_id") Long postId,
                                         @RequestBody ForumComment comment) {
        Long userId = StpUtil.getLoginIdAsLong();
        comment.setPostId(postId);
        comment.setUserId(userId);
        return commentService.createComment(comment);
    }

    @PutMapping("/{id}")
    public ForumCommentDTO updateComment(@PathVariable Long id, @RequestBody ForumComment comment) {
        return commentService.updateComment(id, comment);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
