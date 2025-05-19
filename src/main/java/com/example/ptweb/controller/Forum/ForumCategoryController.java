package com.example.ptweb.controller.Forum;



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

@RestController
@RequestMapping("/forum/categories")
@RequiredArgsConstructor
public class ForumCategoryController {

    private final ForumSectionService categoryService;
//初始化
    @GetMapping
    public List<ForumSection> getAllCategories() {
        return categoryService.getAllSections();
    }
//写贴
    @PostMapping
    public ForumSection createCategory(@RequestBody ForumSection category) {
        return categoryService.createCategory(category);
    }
//更新
    @PutMapping("/{id}")
    public ForumSection updateCategory(@PathVariable Long id, @RequestBody ForumSection category) {
        return categoryService.updateCategory(id, category);
    }
//删除
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
