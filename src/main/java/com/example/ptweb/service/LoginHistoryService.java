package com.example.ptweb.service;

import com.example.ptweb.entity.LoginHistory;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.LoginHistoryMapper;
import com.example.ptweb.type.LoginType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;


@Service
public class LoginHistoryService {

    @Autowired
    private LoginHistoryMapper loginHistoryMapper;

    @NotNull
    public LoginHistory log(@NotNull User user, @NotNull LoginType loginType, @NotNull String ip, @NotNull String userAgent) {
        LoginHistory history = new LoginHistory();
        history.setUserId(user.getId());
        history.setLoginTime(Timestamp.from(Instant.now()));
        history.setLoginType(loginType);
        history.setIpAddress(ip);
        history.setUserAgent(userAgent);

        loginHistoryMapper.insert(history);
        return history;
    }
}

