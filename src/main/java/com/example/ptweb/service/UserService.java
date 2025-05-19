package com.example.ptweb.service;

import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.UserMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

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
}

