package com.example.ptweb.service;

import com.example.ptweb.entity.*;
import com.example.ptweb.exception.AnnounceBusyException;
import com.example.ptweb.type.AnnounceEventType;
import com.example.ptweb.util.ExecutorUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class AnnounceService {

    private final BlockingDeque<AnnounceTask> taskQueue = new LinkedBlockingDeque<>(40960);

    @Autowired
    private ExecutorUtil executor;
    @Autowired
    private UserService userService;
    @Autowired
    private PeerService peerService;
    @Autowired
    private TorrentService torrentService;
    @Autowired
    private AnnouncePerformanceMonitorService monitorService;
    @Autowired
    private TransferHistoryService transferHistoryService;
    @Autowired
    private PromotionService promotionService;

    public AnnounceService() {
        Thread announceHandleThread = new Thread(() -> {
            while (true) {
                try {
                    AnnounceTask announceTask = taskQueue.take();
                    try {
                        long start = System.nanoTime();
                        handleTask(announceTask);
                        monitorService.recordJobStats(System.nanoTime() - start);
                    } catch (Exception e) {
                        log.error("Error handling task: {}", announceTask, e);
                    }
                } catch (InterruptedException e) {
                    log.error("Announce handling thread interrupted", e);
                }
            }
        });
        announceHandleThread.setName("Announce Handling");
        announceHandleThread.setDaemon(true);
        announceHandleThread.start();
    }

    public void schedule(@NotNull AnnounceTask announceTask) throws AnnounceBusyException {
        if (!this.taskQueue.offer(announceTask)) {
            throw new AnnounceBusyException();
        }
    }

    @Transactional
    void handleTask(AnnounceTask task) throws NoSuchElementException {
        // 获取 User
        User user = userService.getUser(task.userId());
        if (user == null) throw new IllegalStateException("User not exists anymore");

        // 获取 Torrent
        Torrent torrent = torrentService.getTorrentById(task.torrentId());
        if (torrent == null) throw new IllegalStateException("Torrent not exists anymore");

        // 获取 Peer
        Peer peer = peerService.getPeer(task.ip(), task.port(), task.infoHash());
        if (peer == null) {
            peer = createNewPeer(task, user);
        }

        long lastUploaded = peer.getUploaded();
        long lastDownloaded = peer.getDownloaded();
        long uploadedOffset = task.uploaded() - lastUploaded;
        long downloadedOffset = task.downloaded() - lastDownloaded;
        Timestamp lastUpdateAt = torrent.getUpdatedAt();

        if (uploadedOffset < 0) uploadedOffset = task.uploaded();
        if (downloadedOffset < 0) downloadedOffset = task.downloaded();

        long nowMillis = Instant.now().toEpochMilli();
        long announceInterval = nowMillis - lastUpdateAt.toInstant().toEpochMilli();

        peer.setUploaded(task.uploaded() + uploadedOffset);
        peer.setDownloaded(task.downloaded() + downloadedOffset);
        peer.setLeft(task.left());
        peer.setSeeder(task.left() == 0);
        peer.setUpdateAt(Timestamp.from(Instant.now()));
        peer.setSeedingTime(peer.getSeedingTime() + announceInterval);
        peer.setPartialSeeder(task.event() == AnnounceEventType.PAUSED);

        long uploadSpeed = announceInterval > 0 ? uploadedOffset / (announceInterval / 1000) : 0;
        long downloadSpeed = announceInterval > 0 ? downloadedOffset / (announceInterval / 1000) : 0;
        peer.setUploadSpeed(uploadSpeed);
        peer.setDownloadSpeed(downloadSpeed);
        peerService.save(peer);

        // 更新 User
        user.setRealDownloaded(user.getRealDownloaded() + lastDownloaded);
        user.setRealUploaded(user.getRealUploaded() + lastUploaded);
//        long promoUp = (long) user.getGroup().getPromotionPolicy().applyUploadRatio(lastDownloaded);
//        long promoDown = (long) user.getGroup().getPromotionPolicy().applyDownloadRatio(lastUploaded);
        PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getId());
        long promoUp = (long) promotionPolicy.applyUploadRatio(lastUploaded);
        long promoDown = (long) promotionPolicy.applyDownloadRatio(lastDownloaded);

        user.setUploaded(user.getUploaded() + promoUp);
        user.setDownloaded(user.getDownloaded() + promoDown);
        user.setSeedingTime(user.getSeedingTime() + announceInterval);
        userService.save(user);

        // 更新 TransferHistory
        TransferHistory history = transferHistoryService.getTransferHistory(user, torrent);
        if (history != null) {
            if (history.getLeft() != 0 && task.left() == 0) {
                history.setHaveCompleteHistory(true);
            }
            history.setUpdatedAt(Timestamp.from(Instant.now()));
            history.setLeft(task.left());
            history.setUploaded(history.getUploaded() + promoUp);
            history.setDownloaded(history.getDownloaded() + promoDown);
            history.setActualUploaded(history.getActualUploaded() + uploadedOffset);
            history.setActualDownloaded(history.getActualDownloaded() + downloadedOffset);
            history.setUploadSpeed(uploadSpeed);
            history.setDownloadSpeed(downloadSpeed);
        } else {
            history = new TransferHistory(null, user.getId(), torrent.getId(),
                    task.left(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    promoUp, promoDown, uploadedOffset, downloadedOffset,
                    uploadSpeed, downloadSpeed, task.event(), false);
        }
        transferHistoryService.save(history);

        // 更新 Torrent
        torrentService.save(torrent);

        // 处理 STOPPED 事件
        if (task.event() == AnnounceEventType.STOPPED) {
            peerService.delete(peer);
        }
    }

    @NotNull
    private Peer createNewPeer(AnnounceTask task, User user) {
        return new Peer(
                0,
                task.ip(),
                task.port(),
                task.infoHash().toLowerCase(),
                task.peerId(),
                task.userAgent(),
                task.uploaded(),
                task.downloaded(),
                task.left(),
                task.left() == 0,
                task.event() == AnnounceEventType.PAUSED,
                task.passKey(),
                Timestamp.from(Instant.now()),
                0,
                0,
                0,
                user
        );
    }

    public record AnnounceTask(
            @NotNull String ip, int port, @NotNull String infoHash, @NotNull String peerId,
            long uploaded, long downloaded, long left, @NotNull AnnounceEventType event,
            int numWant, long userId, boolean compact, boolean noPeerId,
            boolean supportCrypto, int redundant, String userAgent, String passKey, long torrentId
    ) {
    }
}
