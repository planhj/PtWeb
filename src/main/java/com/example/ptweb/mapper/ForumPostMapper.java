package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumPost;
import com.example.ptweb.entity.ForumSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

    @Update("UPDATE forum_post SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViews(Long postId);

    @Select("SELECT * FROM forum_posts WHERE section_id = #{section_id}")
    List<ForumPost> selectAllposts(Long section_id);


    @Select("SELECT COUNT(*) FROM forum_sections WHERE id = #{id}")
    int existsById(@Param("id") Long id);




}
