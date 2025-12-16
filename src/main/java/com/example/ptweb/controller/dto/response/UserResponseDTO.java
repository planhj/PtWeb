package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.User;
import com.example.ptweb.other.ResponsePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class UserResponseDTO extends ResponsePojo {
    private long id;
    private String email;
    private String username;
    private long createdAt;
    private String avatar;
    private String customTitle;
    private String signature;
    private String language;
    private long downloaded;
    private long uploaded;
    private long realDownloaded;
    private long realUploaded;
    private BigDecimal bonusPoints;
    private long seedingTime;

    public UserResponseDTO(@NotNull User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.createdAt = System.currentTimeMillis()-user.getCreateAt().getTime();
        this.avatar = user.getAvatar();
        this.customTitle = user.getCustomTitle().getDescription();
        this.signature = user.getSignature();
        this.downloaded = user.getDownloaded();
        this.uploaded = user.getUploaded();
        this.realDownloaded = user.getRealDownloaded();
        this.realUploaded = user.getRealUploaded();
        this.bonusPoints = user.getScore();
        this.seedingTime = user.getSeedingTime();
    }
}
