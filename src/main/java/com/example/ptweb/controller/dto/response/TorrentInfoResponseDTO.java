package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.Category;
import com.example.ptweb.entity.PromotionPolicy;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.entity.User;
import com.example.ptweb.other.ResponsePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.List;

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
    private PromotionPolicy promotionPolicy;
    private String description;
    private final List<String> tag;;


    public TorrentInfoResponseDTO(Torrent torrent, User user, Category category, PromotionPolicy promotionPolicy, List<String> tagList) {
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
            if (user != null) {
                this.user = new UserResponseDTO(user);
            }
        }
        if (category != null) {
            this.category = new CategoryResponseDTO(category);
        }
        if(promotionPolicy != null) {
            this.promotionPolicy=promotionPolicy;
        }

        this.tag = tagList;
    }
}