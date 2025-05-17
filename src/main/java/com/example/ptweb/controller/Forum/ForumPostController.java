package com.example.ptweb.controller.Forum;



import com.example.ptweb.entity.ForumPost;
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
@RequestMapping("/api/forum/posts")
@RequiredArgsConstructor
public class ForumPostController {

    private final ForumPostService postService;

    @GetMapping
    public List<ForumPost> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ForumPost getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PostMapping
    public ForumPost createPost(@RequestBody ForumPost post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}")
    public ForumPost updatePost(@PathVariable Long id, @RequestBody ForumPost post) {
        return postService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @PostMapping("/{id}/increment-views")
    public void incrementViews(@PathVariable Long id) {
        postService.incrementViews(id);
    }
}
