package com.example.ptweb.service;

import com.example.ptweb.entity.EmailChangeToken;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.mapper.EmailChangeTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailChangeTokenService {

    @Autowired
    private EmailChangeTokenMapper emailChangeTokenMapper;

    public void saveToken(Long userId, String newEmail, String token, LocalDateTime expireTime) {
        EmailChangeToken emailChangeToken = new EmailChangeToken();
        emailChangeToken.setUserId(userId);
        emailChangeToken.setNewEmail(newEmail);
        emailChangeToken.setToken(token);
        emailChangeToken.setExpireTime(expireTime);
        emailChangeTokenMapper.insert(emailChangeToken);
    }

    public EmailChangeToken getByToken(String token) {
        return emailChangeTokenMapper.selectOne(new QueryWrapper<EmailChangeToken>().eq("token", token));
    }

    public void deleteByToken(String token) {
        emailChangeTokenMapper.delete(new QueryWrapper<EmailChangeToken>().eq("token", token));
    }
}