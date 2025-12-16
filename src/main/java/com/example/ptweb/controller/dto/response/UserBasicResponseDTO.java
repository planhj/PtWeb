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
public class UserBasicResponseDTO extends ResponsePojo {
    private long id;
    private String username;
    private long createAt;
    private String avatar;
    private String customTitle;
    private String signature;
    private long downloaded;
    private long uploaded;
    private double bonusPoints;
    private long seedingTime;

    public UserBasicResponseDTO(@NotNull User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.createAt = System.currentTimeMillis()-user.getCreateAt().getTime();
        this.avatar = user.getAvatar();
        this.customTitle = user.getCustomTitle().getDescription();
        this.signature = user.getSignature();
        this.downloaded = user.getDownloaded();
        this.uploaded = user.getUploaded();
        this.bonusPoints = user.getScore().doubleValue();
        this.seedingTime = user.getSeedingTime();
    }
}
