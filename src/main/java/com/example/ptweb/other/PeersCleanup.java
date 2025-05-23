package com.example.ptweb.other;

import com.example.ptweb.service.PeerService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PeersCleanup extends QuartzJobBean {
    @Autowired
    private PeerService peerService;

    @Override
    public void executeInternal(@NotNull JobExecutionContext context) {
        log.info("Executing the peers cleanup...");
        int count = peerService.cleanup();
        log.info("Peers cleanup complete! Purged {} peers.", count);
    }
}
