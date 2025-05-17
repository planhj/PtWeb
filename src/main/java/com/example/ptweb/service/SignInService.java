package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.SignIn;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.SignInMapper;
import com.example.ptweb.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.sql.Date;

@Service
public class SignInService {

    private final SignInMapper signInMapper;
    private final UserMapper userMapper;

    public SignInService(SignInMapper signInMapper, UserMapper userMapper) {
        this.signInMapper = signInMapper;
        this.userMapper = userMapper;
    }

    public void signIn(Long userId) {
        LocalDate today = LocalDate.now();

        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 类型转换：Date -> LocalDate
        LocalDate lastSignDate = null;
        if (user.getLastSignInDate() != null) {
            lastSignDate = user.getLastSignInDate().toLocalDate();
        }

        if (lastSignDate != null && lastSignDate.isEqual(today)) {
            throw new RuntimeException("今天已签到");
        }

        int continuousDays = 1;
        if (lastSignDate != null && lastSignDate.plusDays(1).isEqual(today)) {
            continuousDays = user.getContinuousDays() + 1;
        }

        // 计算得分
        int score = 5; // 每次签到基础分
        if (continuousDays == 10) {
            score += 200;
        } else if (continuousDays == 20) {
            score += 500;
        } else if (continuousDays == 30) {
            score += 1000;
        }

        // 更新用户信息
        user.setLastSignInDate(Date.valueOf(today));
        user.setContinuousDays(continuousDays);
        user.setScore(user.getScore().add(new java.math.BigDecimal(score)));
        userMapper.updateById(user);

        // 插入签到记录
        SignIn signIn = new SignIn();
        signIn.setUserId(userId);
        signIn.setSignDate(today);
        signInMapper.insert(signIn);
    }
}