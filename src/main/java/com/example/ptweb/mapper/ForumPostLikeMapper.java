package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumPostLike;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ForumPostLikeMapper extends BaseMapper<ForumPostLike> {
}
