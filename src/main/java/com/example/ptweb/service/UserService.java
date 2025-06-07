package com.example.ptweb.service;

import com.example.ptweb.entity.UserMonthlyStats;
import com.example.ptweb.mapper.UserMonthlyStatsMapper;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.UserMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMonthlyStatsMapper userMonthlyStatsMapper;

    @Nullable
    public User getUser(long id) {
        return userMapper.selectById(id);
    }

    @Nullable
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Nullable
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Nullable
    public User getUserByPasskey(String passkey) {
        return userMapper.findByPasskeyIgnoreCase(passkey);
    }

    @Nullable
    public User getUserByPersonalAccessToken(String personalAccessToken) {
        return userMapper.findByPersonalAccessTokenIgnoreCase(personalAccessToken);
    }

    @NotNull
    public User save(User user) {
        if (user.getId() == 0) {
            // 新建用户
            userMapper.insert(user);
        } else {
            // 更新用户
            userMapper.updateById(user);
        }
        return user;
    }

    @NotNull
    public void updateUser(@NotNull User user) {
        if (user.getId() == 0) {
            throw new IllegalArgumentException("User ID cannot be 0 for update operation.");
        }
        userMapper.updateById(user);
    }

    public void addUploadAndSeeding(long userId, long uploadBytes, long seedingSeconds) {
        String month = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        UserMonthlyStats stats = userMonthlyStatsMapper.selectByUserIdAndMonth(userId, month);
        if (stats == null) {
            stats = new UserMonthlyStats();
            stats.setUserId(userId);
            stats.setMonth(month);
            stats.setUploaded(uploadBytes);
            stats.setSeedingTime(seedingSeconds);
            userMonthlyStatsMapper.insert(stats);
        } else {
            stats.setUploaded(stats.getUploaded() + uploadBytes);
            stats.setSeedingTime(stats.getSeedingTime() + seedingSeconds);
            userMonthlyStatsMapper.updateById(stats);
        }
    }

    // 每月1号凌晨1点自动考核
    @Scheduled(cron = "0 0 1 1 * ?")
    public void monthlyAssessment() {
        assessUsers();
    }

    // 手动考核方法，供 Controller 调用
    public void assessUsers() {
        long minUploadBytes = 50L * 1024 * 1024 * 1024; // 50GB
        long minSeedingSeconds = 100L * 3600; // 100小时

        List<User> users = userMapper.selectNormalUsers();
        for (User user : users) {
            if (user.getRealUploaded() < minUploadBytes || user.getSeedingTime() < minSeedingSeconds) {
                user.setStatus("banned");
                userMapper.updateById(user);
            }
        }
    }


    @Scheduled(cron = "0 5 0 1 * ?") // 每月1号0:05执行
    public void initNewMonthStats() {
        String newMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Long> userIds = userMonthlyStatsMapper.selectAllUserIds();
        for (Long userId : userIds) {
            if (userMonthlyStatsMapper.selectByUserIdAndMonth(userId, newMonth) == null) {
                UserMonthlyStats stats = new UserMonthlyStats();
                stats.setUserId(userId);
                stats.setMonth(newMonth);
                stats.setUploaded(0L);
                stats.setSeedingTime(0L);
                userMonthlyStatsMapper.insert(stats);
            }
        }
    }

    // UserService.java
    public UserMonthlyStats getCurrentMonthStats(long userId) {
        String month = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        return userMonthlyStatsMapper.selectByUserIdAndMonth(userId, month);
    }
}


