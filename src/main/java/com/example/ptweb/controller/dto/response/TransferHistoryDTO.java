package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.Torrent;
import com.example.ptweb.entity.TransferHistory;
import com.example.ptweb.entity.User;
import com.example.ptweb.service.TorrentService;
import com.example.ptweb.service.UserService;
import com.example.ptweb.type.PrivacyLevel;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;

@Data
@Validated
public class TransferHistoryDTO {
    private long id;
    private UserBasicResponseDTO user;
    private TorrentBasicResponseDTO torrent;
    private long left;
    private Timestamp startedAt;
    private Timestamp updatedAt;
    private long uploaded;
    private long downloaded;
    private long actualUploaded;
    private long actualDownloaded;
    private long uploadSpeed;
    private long downloadSpeed;

    public TransferHistoryDTO(TransferHistory transferHistory) {
        this.id = transferHistory.getId();
        UserService userService = new UserService();
        User user1 = userService.getUser(transferHistory.getUserId());
        if (user1 != null && user1.getPrivacyLevel().ordinal() > PrivacyLevel.MEDIUM.ordinal()) {
            this.user = null;
        }
        TorrentService torrentService = new TorrentService();
        Torrent torrent1 = torrentService.getTorrentById(transferHistory.getTorrentId());
        this.torrent = new TorrentBasicResponseDTO(torrent1);
        this.left = transferHistory.getLeft();
        this.startedAt = transferHistory.getStartedAt();
        this.updatedAt = transferHistory.getUpdatedAt();
        this.uploaded = transferHistory.getUploaded();
        this.downloaded = transferHistory.getDownloaded();
        this.actualUploaded = transferHistory.getActualUploaded();
        this.actualDownloaded = transferHistory.getActualDownloaded();
        this.uploadSpeed = transferHistory.getUploadSpeed();
        this.downloadSpeed = transferHistory.getDownloadSpeed();

    }
}
