package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.*;
import com.example.ptweb.other.ResponsePojo;
import com.example.ptweb.service.CategoryService;
import com.example.ptweb.service.UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class TorrentInfoResponseDTO extends ResponsePojo {
    private long id;
    private String infoHash;
    private UserResponseDTO user;
    private String title;
    private String subTitle;
    private long size;
    private long finishes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean underReview;
    private CategoryResponseDTO category;
    private Object promotionPolicy; // 你可以保留原 PromotionPolicy 或换成 DTO
    private String description;
    private List<String> tag; // 这里改为 tag 名称列表


    public TorrentInfoResponseDTO(Torrent torrent, Map<Long, Tag> tagMap) {
        super(0);
        this.id = torrent.getId();
        this.infoHash = torrent.getInfoHash();
        this.title = torrent.getTitle();
        this.subTitle = torrent.getSubTitle();
        this.size = torrent.getSize();
        this.createdAt = torrent.getCreatedAt();
        this.updatedAt = torrent.getUpdatedAt();
        this.underReview = torrent.isUnderReview();
        this.description = torrent.getDescription();
        this.finishes = 0; // 可自行设置

        if (torrent.isAnonymous()) {
            this.user = null;
        } else {
            UserService userService = new UserService();
            User user = userService.getUser(torrent.getUserId());
            if (user != null) {
                this.user = new UserResponseDTO(user);
            }
        }
        CategoryService categoryService = new CategoryService();
        Category category = categoryService.getCategory(torrent.getCategoryId());
        if (category != null) {
            this.category = new CategoryResponseDTO(category);
        }
        this.promotionPolicy = torrent.getPromotionPolicyId();

        // 转换 tag ID 列表为名称列表
        this.tag = torrent.getTag() == null ? List.of() :
                torrent.getTag().stream()
                        .map(tagMap::get)
                        .filter(tag -> tag != null)
                        .map(Tag::getName)
                        .collect(Collectors.toList());
    }
}