package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumCcomment;
import com.example.ptweb.entity.ForumComment;
import com.example.ptweb.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ForumCcommentMapper extends BaseMapper<ForumCcomment> {
    @Select("SELECT * FROM forum_ccomments WHERE comment_id = #{commentId} ORDER BY created_at ASC")
    List<ForumCcomment> findBycommentId(@Param("commentId") Long commentId);


}
