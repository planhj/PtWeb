package com.example.ptweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.ForumSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface ForumSectionMapper extends BaseMapper<ForumSection> {
    @Select("SELECT * FROM forum_sections")
    List<ForumSection> selectAllCategories();

}
