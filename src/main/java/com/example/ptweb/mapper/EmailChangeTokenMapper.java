package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.EmailChangeToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailChangeTokenMapper extends BaseMapper<EmailChangeToken> {
}