package com.example.ptweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.Torrent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TorrentMapper extends BaseMapper<Torrent> {
}