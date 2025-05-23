package com.example.ptweb.controller.torrent.dto.response;

import com.example.ptweb.controller.dto.response.TorrentBasicResponseDTO;
import com.example.ptweb.other.ResponsePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class TorrentSearchResultResponseDTO extends ResponsePojo {
    private long totalElements;
    private long totalPages;
    private List<TorrentBasicResponseDTO> torrents;

    public TorrentSearchResultResponseDTO(long totalElements, long totalPages, List<TorrentBasicResponseDTO> torrents) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.torrents = torrents;
    }
}

