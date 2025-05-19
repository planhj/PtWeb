package com.example.ptweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ptweb.entity.Peer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;


@Mapper
public interface PeerMapper extends BaseMapper<Peer> {

    @Select("SELECT * FROM peers WHERE ip = #{ip} AND port = #{port} AND LOWER(info_hash) = LOWER(#{infoHash}) LIMIT 1")
    Peer selectByIpPortAndInfoHash(@Param("ip") String ip,
                                   @Param("port") int port,
                                   @Param("infoHash") String infoHash);

    @Select("SELECT * FROM peers WHERE user_id = #{userId} AND LOWER(info_hash) = LOWER(#{infoHash}) LIMIT 1")
    Peer selectByUserIdAndInfoHash(@Param("userId") Long userId,
                                   @Param("infoHash") String infoHash);

    @Select("SELECT * FROM peers WHERE LOWER(info_hash) = LOWER(#{infoHash}) ORDER BY update_at DESC LIMIT #{limit}")
    List<Peer> selectPeersByInfoHashOrderByUpdateAtDesc(@Param("infoHash") String infoHash,
                                                        @Param("limit") int limit);

    @Select("SELECT * FROM peers WHERE update_at < #{timestamp}")
    List<Peer> selectAllByUpdateAtBefore(@Param("timestamp") Timestamp timestamp);

    @Delete("DELETE FROM peers WHERE LOWER(info_hash) = LOWER(#{infoHash}) AND peer_id = #{peerId}")
    int deleteByInfoHashAndPeerId(@Param("infoHash") String infoHash,
                                  @Param("peerId") String peerId);
}
