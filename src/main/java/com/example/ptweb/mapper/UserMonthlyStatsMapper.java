package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.UserMonthlyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMonthlyStatsMapper extends BaseMapper<UserMonthlyStats> {

    @Select("SELECT * FROM user_monthly_stats WHERE user_id = #{userId} AND month = #{month} LIMIT 1")
    UserMonthlyStats selectByUserIdAndMonth(@Param("userId") long userId, @Param("month") String month);

    @Select("SELECT DISTINCT user_id FROM user_monthly_stats")
    List<Long> selectAllUserIds();
}