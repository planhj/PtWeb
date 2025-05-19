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
import java.util.Map;


@RestController
@RequestMapping("/forum/posts")
@RequiredArgsConstructor
public class ForumPostController {

    private final ForumPostService postService;

    @GetMapping("/section/{section_id}")
    public List<ForumPost> getAllPosts(@PathVariable long section_id) {
        return postService.getAllPosts(section_id);
    }

//    @GetMapping("/{id}")
//  public ForumPost getPostById(@PathVariable Long id) {
//      return postService.getPostById(id);
//   }

    @PostMapping("/section/{section_id}/{user_id}")
    public ForumPost createPost(@PathVariable("section_id") Long sectionId,
                                @PathVariable("user_id") Long userId,
                                @RequestBody ForumPost post) {
        post.setSectionId(sectionId);
        post.setUserId(userId);
        return postService.createPost(post);
    }



/**  public ResponseEntity<?> createPost(@RequestBody ForumPost post) {
        try {
            ForumPost savedPost = postService.createPost(post);
            return ResponseEntity.ok(savedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }**/

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
