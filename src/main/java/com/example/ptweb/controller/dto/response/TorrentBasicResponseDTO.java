package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.*;
import com.example.ptweb.other.ResponsePojo;
import com.example.ptweb.service.CategoryService;
import com.example.ptweb.service.PromotionService;
import com.example.ptweb.service.UserService;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Validated
public class TorrentBasicResponseDTO extends ResponsePojo {
    private final long id;
    private final String infoHash;
    private final long userId;
    private final String title;
    private final String subTitle;
    private final long size;
    private final Timestamp createdAt;
    private final boolean underReview;
    private final boolean anonymous;
    private final long categoryId;
    private final long promotionPolicyId;
    private final List<String> tag;

    public TorrentBasicResponseDTO(Torrent torrent){
        super(0);
        this.id = torrent.getId();
        this.infoHash = torrent.getInfoHash();
        PromotionService promotionService = new PromotionService();
        PromotionPolicy promotionPolicy1 = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());

        if(torrent.isAnonymous()){
            this.userId = 0L;
        }else{
            this.userId = torrent.getUserId();
        }
        this.title = torrent.getTitle();
        this.subTitle = torrent.getSubTitle();
        this.size = torrent.getSize();
        this.createdAt = torrent.getCreatedAt();
        this.underReview = torrent.isUnderReview();
        this.anonymous = torrent.isAnonymous();
        this.categoryId = torrent.getCategoryId();
        this.promotionPolicyId = torrent.getPromotionPolicyId();
        this.tag = null; //torrent.getTag().stream().map(Tag::getName).toList();
    }
}
