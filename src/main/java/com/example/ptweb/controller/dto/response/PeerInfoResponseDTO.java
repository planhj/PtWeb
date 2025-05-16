package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.Peer;
import com.example.ptweb.entity.User;
import com.example.ptweb.other.ResponsePojo;
import com.example.ptweb.service.UserService;
import com.example.ptweb.type.PrivacyLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class PeerInfoResponseDTO extends ResponsePojo {
    private long id;
    private UserBasicResponseDTO userDto;
    private String infoHash;
    private String peerId;
    private String userAgent;
    private long uploaded;
    private long downloaded;
    private long left;
    private boolean seeder;
    private boolean partialSeeder;
    private Timestamp updateAt;
    private long seedingTime;
    private long uploadSpeed;
    private long downloadSpeed;
    private UserService userService;
    private User user;

    public PeerInfoResponseDTO(Peer peer) {
        this.id = peer.getId();
        this.user = userService.getUser(peer.getUserId());
        if (this.user.getPrivacyLevel().ordinal() > PrivacyLevel.MEDIUM.ordinal()) {
            this.userDto = null;
        } else {
            this.userDto = new UserBasicResponseDTO(this.user);
        }
        this.infoHash = peer.getInfoHash();
        this.peerId = peer.getPeerId();
        this.userAgent = peer.getUserAgent();
        this.uploaded = peer.getUploaded();
        this.downloaded = peer.getDownloaded();
        this.left = peer.getToGo();
        this.seeder = peer.isSeeder();
        this.partialSeeder = peer.isPartialSeeder();
        this.updateAt = peer.getUpdateAt();
        this.seedingTime = peer.getSeedingTime();
        this.uploadSpeed = peer.getUploadSpeed();
        this.downloadSpeed = peer.getDownloadSpeed();
    }
}
