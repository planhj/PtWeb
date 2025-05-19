package com.example.ptweb.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Getter
@Component

public class AnnouncePerformanceMonitorService {
    private final Instant startTime = Instant.now();
    private final Cache<UUID, Long> announceTimes = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .build();
    private final Cache<UUID, Long> announceJobTimes = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .build();
    private long handled = 0;

    public void recordStats(long ns) {
        announceTimes.put(UUID.randomUUID(), ns);
        handled++;
    }

    public void recordJobStats(long ns) {
        announceJobTimes.put(UUID.randomUUID(), ns);
    }

    public double avgNs() {
        return announceTimes.asMap().values().stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public double avgJobMs() {
        return avgJobNs() / 1000000;
    }

    public double avgJobNs() {
        return announceJobTimes.asMap().values().stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public double avgMs() {
        return avgNs() / 1000000;
    }

}
