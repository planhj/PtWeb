package com.example.ptweb.service;

import com.example.ptweb.entity.Peer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ptweb.mapper.PeerMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class PeerService {

    @Autowired
    private PeerMapper peerMapper;

    @Nullable
    public Peer getPeer(@NotNull String ip, int port, @NotNull String infoHash) {
        infoHash = infoHash.toLowerCase(Locale.ROOT);
        return peerMapper.selectByIpPortAndInfoHash(ip, port, infoHash);
    }

    @NotNull
    public List<Peer> getPeers(@NotNull String infoHash, int numWant) {
        infoHash = infoHash.toLowerCase(Locale.ROOT);
        return peerMapper.selectPeersByInfoHashOrderByUpdateAtDesc(infoHash, numWant);
    }

    @NotNull
    public Peer save(@NotNull Peer peer) {
        peer.setInfoHash(peer.getInfoHash().toLowerCase(Locale.ROOT));
        if (peer.getId() == 0) {
            peerMapper.insert(peer);
        } else {
            peerMapper.updateById(peer);
        }
        return peer;
    }

    public void delete(@NotNull Peer peer) {
        peerMapper.deleteById(peer.getId());
    }

    public int cleanup() {
        Timestamp cutoff = Timestamp.from(Instant.now().minus(90, ChronoUnit.MINUTES));
        List<Peer> stalePeers = peerMapper.selectAllByUpdateAtBefore(cutoff);
        int count = stalePeers.size();
        for (Peer peer : stalePeers) {
            peerMapper.deleteById(peer.getId());
        }
        return count;
    }

    public void deleteByInfoHashAndPeerId(@NotNull String infoHash, @NotNull String peerId) {
        peerMapper.deleteByInfoHashAndPeerId(infoHash.toLowerCase(Locale.ROOT), peerId);
    }
}

