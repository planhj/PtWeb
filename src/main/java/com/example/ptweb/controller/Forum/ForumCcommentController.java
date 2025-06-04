package com.example.ptweb.controller.Forum;



import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.controller.Forum.dto.ForumCcommentDTO;
import com.example.ptweb.entity.ForumCcomment;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ptweb.entity.item_categories;
import com.example.ptweb.service.itemService.PurchaseResult;
import com.example.ptweb.service.itemService.BusinessException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/forum/ccomments")
@RequiredArgsConstructor
public class ForumCcommentController {

    private final ForumCcommentService ccommentService;

    @GetMapping("/{post_id}/{comment_id}")
    public List<ForumCcommentDTO> getCcommentsByComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId) {
        // 这里暂时没用postId，传给service commentId即可
        return ccommentService.getCcommentDTOsByCommentId(commentId);
    }

    @PostMapping("/{post_id}/{comment_id}")
    public ForumCcommentDTO createComment(@PathVariable("post_id") Long postId,
                                          @PathVariable("comment_id") Long comentId,
                                          @RequestBody ForumCcomment ccomment) {
        Long userId = StpUtil.getLoginIdAsLong();
        ccomment.setCommentId(comentId);
        ccomment.setUserId(userId);
        return ccommentService.createCcomment(ccomment, postId);
    }

    @PutMapping("/{id}")
    public ForumCcomment updateComment(@PathVariable Long id, @RequestBody ForumCcomment ccomment) {
        return ccommentService.updateCcomment(id, ccomment);
    }

    @DeleteMapping("/{id}")
    public void deleteCcomment(@PathVariable Long id) {
        ccommentService.deleteCcomment(id);
    }
}
