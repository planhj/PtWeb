package com.example.ptweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ptweb.entity.InviteCode;
import com.example.ptweb.entity.User;
import com.example.ptweb.mapper.InviteCodeMapper;
import com.example.ptweb.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteCodeService {

    private final InviteCodeMapper inviteCodeMapper;
    private final UserMapper userMapper;

    public List<InviteCode> getInviteCodesByCreatorId(Long userId) {
        return inviteCodeMapper.selectList(
                new QueryWrapper<InviteCode>().eq("creator_id", userId).eq("used", false)
        );
    }

    @Transactional
    public String generateInviteCode(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查积分是否足够
        if (user.getScore().compareTo(BigDecimal.valueOf(500)) < 0) {
            throw new IllegalStateException("积分不足，无法生成邀请码（需要500积分）");
        }

        // 扣除500积分
        user.setScore(user.getScore().subtract(BigDecimal.valueOf(500)));
        userMapper.updateById(user);

        // 生成唯一邀请码
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 10); // 截取10位
        } while (inviteCodeMapper.selectOne(new QueryWrapper<InviteCode>().eq("code", code)) != null);

        // 保存邀请码
        InviteCode inviteCode = new InviteCode();
        inviteCode.setCode(code);
        inviteCode.setCreatorId(userId);
        inviteCode.setUsed(false);
        inviteCode.setCreateTime(LocalDateTime.now());
        inviteCodeMapper.insert(inviteCode);

        return inviteCode.getCode();
    }
}
