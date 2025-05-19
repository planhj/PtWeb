package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.TransferHistory;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;

@Data
@Validated
public class TransferHistoryDTO {
    private long id;
    private UserBasicResponseDTO user;
    private long torrentId;
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
        this.user = null;
        this.torrentId=transferHistory.getTorrentId();
        this.left = transferHistory.getToGo();
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
