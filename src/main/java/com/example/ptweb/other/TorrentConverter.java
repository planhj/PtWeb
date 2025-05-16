package com.example.ptweb.other;


import com.example.ptweb.controller.dto.response.CategoryResponseDTO;
import com.example.ptweb.controller.dto.response.TorrentBasicResponseDTO;
import com.example.ptweb.entity.Category;
import com.example.ptweb.entity.PromotionPolicy;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.service.CategoryService;
import com.example.ptweb.service.PromotionService;
import com.example.ptweb.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TorrentConverter {

    private final CategoryService categoryService;
    private final PromotionService promotionService;
    private final TagService tagService;

    @Autowired
    public TorrentConverter(CategoryService categoryService,
                            PromotionService promotionService,
                            TagService tagService) {
        this.categoryService = categoryService;
        this.promotionService = promotionService;
        this.tagService = tagService;
    }

    public TorrentBasicResponseDTO convert(Torrent torrent) {
        // 获取分类信息
        Category category = categoryService.getCategory(torrent.getCategoryId());
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO(category);

        // 获取促销策略信息
        PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());

        // 获取标签名列表
        List<String> tagNames = tagService.getTagNamesByIds(torrent.getTag());

        // 构造 DTO
        return new TorrentBasicResponseDTO(torrent, categoryDTO, promotionPolicy, tagNames);
    }
}