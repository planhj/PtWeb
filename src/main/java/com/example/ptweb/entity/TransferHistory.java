package com.example.ptweb.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.example.ptweb.type.AnnounceEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("transfer_history")
public class TransferHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("torrent_id")
    private Long torrentId;

    @TableField("to_go")
    private long toGo;

    @TableField("started_at")
    private Timestamp startedAt;

    @TableField("updated_at")
    private Timestamp updatedAt;

    @TableField("uploaded")
    private long uploaded;

    @TableField("downloaded")
    private long downloaded;

    @TableField("actual_uploaded")
    private long actualUploaded;

    @TableField("actual_downloaded")
    private long actualDownloaded;

    @TableField("upload_speed")
    private long uploadSpeed;

    @TableField("download_speed")
    private long downloadSpeed;

    @TableField("last_event")
    private AnnounceEventType lastEvent;

    @TableField("have_complete_history")
    private boolean haveCompleteHistory;

}
