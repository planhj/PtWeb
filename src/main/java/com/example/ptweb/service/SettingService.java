package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.ptweb.entity.SettingEntity;
import com.example.ptweb.exception.BadConfigException;
import com.example.ptweb.mapper.SettingMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
@Slf4j
public class SettingService {
    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @NotNull
    public <T> T get(@NotNull String configKey, @NotNull Class<T> clazz) throws BadConfigException {
        QueryWrapper<SettingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setting_key", configKey);

        SettingEntity configData = settingMapper.selectOne(queryWrapper);

        // 统一：先尝试 resolve 默认配置
        T entity = null;
        if (configData != null) {
            try {
                entity = objectMapper.readValue(configData.getSettingValue(), clazz);
            } catch (JsonProcessingException e) {
                log.error("反序列化配置失败: {} -> {}", configKey, configData.getSettingValue(), e);
                throw new RuntimeException(e);
            }
        } else {
            log.warn("配置项 {} 不存在，尝试调用 spawnDefault 创建默认值。", configKey);
            entity = resolve(clazz);
            if (entity == null) throw new BadConfigException();
        }

        // 始终执行一次更新（用于修复默认值、新字段等）
        try {
            set(configKey, entity);
        } catch (JsonProcessingException e) {
            log.error("更新配置项失败: {} -> {}", configKey, entity, e);
            throw new RuntimeException(e);
        }

        return entity;
    }

    @Nullable
    private <T> T resolve(Class<T> clazz) {
        try {
            Method method = clazz.getDeclaredMethod("spawnDefault");
            return (T) method.invoke(null);
        } catch (Throwable e) {
            log.error("Failed to resolve {}", clazz.getName(), e);
            return null;
        }
    }

    public <T> void set(@NotNull String configKey, @Nullable T value) throws JsonProcessingException {
        QueryWrapper<SettingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setting_key", configKey);

        if (value == null) {
            settingMapper.delete(queryWrapper);
            return;
        }

        SettingEntity entity = settingMapper.selectOne(queryWrapper);
        String json = objectMapper.writeValueAsString(value);

        if (entity != null) {
            entity.setSettingValue(json);
            settingMapper.updateById(entity);
        } else {
            entity = new SettingEntity(0, configKey, json);
            settingMapper.insert(entity);
        }
    }
}
