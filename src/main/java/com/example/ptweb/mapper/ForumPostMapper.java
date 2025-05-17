package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;



@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

    @Update("UPDATE forum_post SET views = views + 1 WHERE id = #{postId}")
    void incrementViews(Long postId);
}
