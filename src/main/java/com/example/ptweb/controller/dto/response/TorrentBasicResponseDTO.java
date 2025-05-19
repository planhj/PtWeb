package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.Category;
import com.example.ptweb.entity.PromotionPolicy;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.other.ResponsePojo;
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
    private final Category category;
    private final PromotionPolicy promotionPolicy;
    private final List<String> tag;
    private final long seederCount;
    private final long leecherCount;
    private final long completedCount;

    public TorrentBasicResponseDTO(
            Torrent torrent,
            Category category,
            PromotionPolicy promotionPolicy,
            List<String> tagNames
    ) {
        super(0);
        this.id = torrent.getId();
        this.infoHash = torrent.getInfoHash();
        this.userId = torrent.isAnonymous() ? 0L : torrent.getUserId();
        this.title = torrent.getTitle();
        this.subTitle = torrent.getSubTitle();
        this.size = torrent.getSize();
        this.createdAt = torrent.getCreatedAt();
        this.underReview = torrent.isUnderReview();
        this.anonymous = torrent.isAnonymous();
        this.category = category;
        this.promotionPolicy = promotionPolicy;
        this.tag = tagNames;
        this.seederCount = torrent.getSeederCount();
        this.leecherCount = torrent.getLeecherCount();
        this.completedCount =torrent.getCompletedCount();
    }

}
