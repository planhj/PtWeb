package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.PasswordResetToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface PasswordResetTokenMapper extends BaseMapper<PasswordResetToken> {
    // 通过token查找
    @Select("SELECT * FROM password_reset_token WHERE token = #{token} LIMIT 1")
    PasswordResetToken selectByToken(String token);

    // 通过userId查找
    @Select("SELECT * FROM password_reset_token WHERE user_id = #{userId} LIMIT 1")
    PasswordResetToken selectByUserId(Long userId);

    // 删除过期token
    @Delete("DELETE FROM password_reset_token WHERE expiry_date < #{now}")
    int deleteExpiredTokens(Date now);
}
