package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
    @Select("SELECT * FROM forum_comments WHERE post_id = #{postId} ORDER BY created_at ASC")
    List<ForumComment> findByPostId(@Param("postId") Long postId);


}
