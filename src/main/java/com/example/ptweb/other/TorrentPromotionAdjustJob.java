package com.example.ptweb.other;

import com.example.ptweb.entity.Torrent;
import com.example.ptweb.service.TorrentService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class TorrentPromotionAdjustJob extends QuartzJobBean {

    @Autowired
    private TorrentService torrentService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        log.info("开始执行 promotion_policy_id 调整任务");

        List<Torrent> allTorrents = torrentService.getAllTorrents(); // 你需要有一个查询全部种子的方法

        int updated = 0;
        long nowMillis = Instant.now().toEpochMilli();

        for (Torrent torrent : allTorrents) {
            long createdMillis = torrent.getCreatedAt().getTime();
            boolean isNew = nowMillis - createdMillis < 24 * 60 * 60 * 1000;
            boolean lowSeeder = torrent.getSeederCount() < 4;
            long currentPolicy = torrent.getPromotionPolicyId();
            if (isNew) {
                torrent.setPromotionPolicyId(2L);
                updated++;
            } else if (lowSeeder) {
                torrent.setPromotionPolicyId(1L);
                updated++;
            }else{
                torrent.setPromotionPolicyId(3L);
                updated++;
            }
        }

        log.info("调整完成，共更新 {} 个种子的 promotion_policy_id", updated);
        torrentService.saveAll(allTorrents); // 你需要有一个批量保存的方法，或遍历单独保存
    }
}

