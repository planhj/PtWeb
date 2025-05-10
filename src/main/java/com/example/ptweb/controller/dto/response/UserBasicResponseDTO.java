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
    private String downloadBandwidth;
    private String uploadBandwidth;
    private long downloaded;
    private long uploaded;
    private BigDecimal score;
    private long seedingTime;

    public UserBasicResponseDTO(@NotNull User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.createAt = user.getCreateAt().getTime();
        this.avatar = user.getAvatar();
        this.customTitle = user.getCustomTitle();
        this.signature = user.getSignature();
        this.downloadBandwidth = user.getDownloadBandwidth();
        this.uploadBandwidth = user.getUploadBandwidth();
        this.downloaded = user.getDownloaded();
        this.uploaded = user.getUploaded();
        this.score = user.getScore();
        this.seedingTime = user.getSeedingTime();
    }
}
