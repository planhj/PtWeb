package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.LoginHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface LoginHistoryMapper extends BaseMapper<LoginHistory> {

    List<LoginHistory> findAllByIpAddress(String ipAddress);

    List<LoginHistory> findAllByUserAgent(String userAgent);

    List<LoginHistory> findAllByLoginTimeBetween(Timestamp start, Timestamp end);

    @Select("SELECT * FROM login_history WHERE user_id = #{userId} ORDER BY time DESC")
    List<LoginHistory> findAllByUserIdOrderByLoginTimeDesc(Long userId);
}
