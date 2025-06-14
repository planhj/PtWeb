package com.example.ptweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.Torrent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TorrentMapper extends BaseMapper<Torrent> {
    @Select("SELECT id FROM torrent ORDER BY completed_count DESC LIMIT 10")
    List<Long> selectTop10ByCompletedCount();
}