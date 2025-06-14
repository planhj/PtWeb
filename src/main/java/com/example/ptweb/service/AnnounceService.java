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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
        Peer peer = peerService.getPeer(task.userId(), task.infoHash());
        if (peer == null) {
            peer = createNewPeer(task, user);
        }

        long lastUploaded = peer.getUploaded();
        long lastDownloaded = peer.getDownloaded();
        long uploadedOffset = task.uploaded() - lastUploaded;
        long downloadedOffset = task.downloaded() - lastDownloaded;
        Timestamp lastUpdateAt = peer.getUpdateAt();
        if (uploadedOffset < 0) uploadedOffset = task.uploaded();
        if (downloadedOffset < 0) downloadedOffset = task.downloaded();

        long nowMillis = Instant.now().toEpochMilli();
        long announceInterval = nowMillis - lastUpdateAt.toInstant().toEpochMilli();

        peer.setUploaded(lastUploaded+ uploadedOffset);
        peer.setDownloaded(lastDownloaded + downloadedOffset);
        peer.setToGo(task.left());
        peer.setSeeder(task.left() == 0);
        peer.setUpdateAt(Timestamp.from(Instant.now()));
        peer.setSeedingTime(peer.getSeedingTime() + announceInterval);
        peer.setPartialSeeder(task.event() == AnnounceEventType.PAUSED);
        if (task.event() == AnnounceEventType.COMPLETED) {
            torrent.setCompletedCount(torrent.getCompletedCount() + 1);
        }

        long uploadSpeed = uploadedOffset > 0 && announceInterval > 0
                ? (long) (uploadedOffset * 1000.0 / announceInterval)
                : 0;
        long downloadSpeed = downloadedOffset > 0 && announceInterval > 0
                ? (long) (downloadedOffset * 1000.0 / announceInterval)
                : 0;
        peer.setUploadSpeed(uploadSpeed);
        peer.setDownloadSpeed(downloadSpeed);
        peerService.save(peer);

// 更新 User
        user.setRealDownloaded(user.getRealDownloaded() + downloadedOffset);
        user.setRealUploaded(user.getRealUploaded() + uploadedOffset);
        PromotionPolicy promotionPolicy = promotionService.getPromotionPolicy(torrent.getPromotionPolicyId());
        long promoUp = (long) (promotionPolicy.applyUploadRatio(uploadedOffset)*user.getUploadedRatio());
        long promoDown = (long) (promotionPolicy.applyDownloadRatio(downloadedOffset)*user.getDownloadRatio());

        user.setUploaded(user.getUploaded() + promoUp);
        user.setDownloaded(user.getDownloaded() + promoDown);
        user.setSeedingTime(user.getSeedingTime() + announceInterval);

// 新增：统计到月度表
        userService.addUploadAndSeeding(user.getId(), promoUp, announceInterval);
        // 更新 TransferHistory
        TransferHistory history = transferHistoryService.getTransferHistory(user, torrent);
        if (history != null) {
            if (task.left() == 0) {
                history.setHaveCompleteHistory(true);
            }
            history.setUpdatedAt(Timestamp.from(Instant.now()));
            history.setToGo(task.left());
            history.setUploaded(history.getUploaded() + promoUp);
            history.setDownloaded(history.getDownloaded() + promoDown);
            history.setActualUploaded(history.getActualUploaded() + uploadedOffset);
            history.setActualDownloaded(history.getActualDownloaded() + downloadedOffset);
            history.setUploadSpeed(uploadSpeed);
            history.setDownloadSpeed(downloadSpeed);
            history.setLastEvent(task.event());
        } else {
            history = new TransferHistory(null, user.getId(), torrent.getId(),
                    task.left(), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()),
                    promoUp, promoDown, uploadedOffset, downloadedOffset,
                    uploadSpeed, downloadSpeed, task.event(), false,null);
        }
        if (peer.isSeeder()) {
            double torrentSizeGB = torrent.getSize() / 1024.0 / 1024 / 1024.0;
            int weight = (torrent.getSeederCount() < 4) ? 5 : 1;

            long totalSeedingTime = peer.getSeedingTime(); // 总做种时间（累计）
            BigDecimal totalBonus = calculateBonusPoints(totalSeedingTime, torrentSizeGB, weight);

            BigDecimal bonusGiven = history.getBonusGiven() != null ? history.getBonusGiven() : BigDecimal.ZERO;

            BigDecimal delta = totalBonus.subtract(bonusGiven);
            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                user.setScore(user.getScore().add(delta));
                log.info("用户 {} 积分增加：{}", user.getUsername(), delta);
                history.setBonusGiven(bonusGiven.add(delta));
                log.info("transfer{}",history.getBonusGiven());
            }
        }

        userService.save(user);
        transferHistoryService.save(history);
        // 处理 STOPPED 事件
        if (task.event() == AnnounceEventType.STOPPED) {
            peerService.delete(peer);
        }
        List<Peer> peers = peerService.getAllPeersByTorrent(task.infoHash());
        int seederCount = 0, leecherCount = 0;
        long now = System.currentTimeMillis();
        for (Peer p : peers) {
            // 过滤超时的 peer，比如 30 分钟无心跳
            long lastSeen = p.getUpdateAt().getTime();
            if (now - lastSeen <= 30 * 60 * 1000) {
                if (p.isSeeder()) {
                    seederCount++;
                } else {
                    leecherCount++;
                }
            }
        }
        torrent.setSeederCount(seederCount);
        torrent.setLeecherCount(leecherCount);


        // 更新 Torrent
        torrentService.save(torrent);
    }
    private BigDecimal calculateBonusPoints(long seedingTimeMillis, double torrentSizeGB, double weight) {
        final double T0 = 30.0;
        final double B0 = 500.0;

        double Ti = seedingTimeMillis / 1000.0 / 60 / 60;
        double Si = torrentSizeGB;
        log.info("TI{},weight{},seedtime{}",Ti,weight,seedingTimeMillis);

        double A = (1 - Math.pow(10, -Ti / T0)) * Si;
        double rawBonus = B0 * Math.atan(A) * weight;

        return BigDecimal.valueOf(rawBonus).setScale(2, RoundingMode.HALF_UP);
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
                user.getId()
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
