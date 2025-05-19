package com.example.ptweb.controller.torrent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeadSeedTorrentDTO {
    private Long id;
    private String name;
    private String tip;
}