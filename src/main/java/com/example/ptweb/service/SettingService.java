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
        if (configData != null) {
            String data = configData.getSettingValue();
            try {
                return objectMapper.readValue(data, clazz);
            } catch (JsonProcessingException e) {
                log.error("Unable to deserialize setting object: {} -> {}", configKey, data, e);
                throw new RuntimeException(e);
            }
        } else {
            log.error("The configuration key {} doesn't exist in database!", configKey);
            T entity = resolve(clazz);
            if (entity == null) throw new BadConfigException();
            try {
                set(configKey, entity);
                log.info("Resolved missing configuration key via #spawnDefault static method.");
            } catch (JsonProcessingException e) {
                log.error("Unable to serialize setting object: {} -> {}", configKey, entity, e);
                throw new RuntimeException(e);
            }
            return entity;
        }
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
