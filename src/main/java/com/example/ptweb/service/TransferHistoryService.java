package com.example.ptweb.service;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.config.TrackerConfig;
import com.example.ptweb.entity.Torrent;
import com.example.ptweb.entity.TransferHistory;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.TransferHistoryMapper;
import com.example.ptweb.type.AnnounceEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TransferHistoryService {

    @Autowired
    private TransferHistoryMapper mapper;

    @Autowired
    private PeerService peerService;

    @Autowired
    private SettingService settingService;

    public TransferHistory getTransferHistory(User user, Torrent torrent) {
        return mapper.selectOne(new QueryWrapper<TransferHistory>()
                .eq("user_id", user.getId())
                .eq("torrent_id", torrent.getId()));
    }

    public List<TransferHistory> getTransferHistory(Torrent torrent) {
        return mapper.selectList(new QueryWrapper<TransferHistory>()
                .eq("torrent_id", torrent.getId())
                .orderByAsc("updated_at"));
    }

    public List<TransferHistory> getTransferHistoryActive(Torrent torrent) {
        TrackerConfig config = settingService.get(TrackerConfig.getConfigKey(), TrackerConfig.class);
        Timestamp timestamp = Timestamp.from(Instant.now().minus(config.getTorrentIntervalMax() + 15000, ChronoUnit.MILLIS));
        return mapper.selectList(new QueryWrapper<TransferHistory>()
                .eq("torrent_id", torrent.getId())
                .gt("updated_at", timestamp)
                .orderByAsc("updated_at"));
    }

    @Cached(expire = 600, cacheType = CacheType.BOTH)
    public PeerStatus getPeerStatus(Torrent torrent) {
        TrackerConfig config = settingService.get(TrackerConfig.getConfigKey(), TrackerConfig.class);
        List<TransferHistory> histories = getTransferHistory(torrent);
        int complete = 0, incomplete = 0, downloaders = 0, downloaded = 0;

        for (TransferHistory history : histories) {
            if (isTransferActive(history, config)) {
                if (history.getLastEvent() == AnnounceEventType.PAUSED) {
                    downloaders++;
                    continue;
                }
                if (history.isHaveCompleteHistory()) {
                    downloaded++;
                }
                if (history.getToGo() == 0) {
                    complete++;
                } else {
                    incomplete++;
                }
            } else {
                if (history.getToGo() == 0) {
                    complete++;
                }
                if (history.isHaveCompleteHistory()) {
                    downloaded++;
                }
            }
        }
        return new PeerStatus(complete, incomplete, downloaded, downloaders);
    }

    private boolean isTransferActive(TransferHistory history, TrackerConfig config) {
        Timestamp timestamp = Timestamp.from(Instant.now().minus(config.getTorrentIntervalMax() + 15000, ChronoUnit.MILLIS));
        return history.getUpdatedAt().after(timestamp);
    }

    public TransferHistory save(TransferHistory transferHistory) {
        if (transferHistory.getId() == null) {
            mapper.insert(transferHistory);
        } else {
            mapper.updateById(transferHistory);
        }
        return transferHistory;
    }

    public record PeerStatus(int complete, int incomplete, int downloaded, int downloaders) {}
}

