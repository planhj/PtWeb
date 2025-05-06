package com.example.ptweb.controller.torrent.dto.response;

import com.example.ptweb.controller.dto.response.ScrapeContainerDTO;
import com.example.ptweb.controller.dto.response.TransferHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@Validated
public class TorrentScrapeResponseDTO {
    private Map<String, ScrapeContainerDTO> scrapes;
    private Map<String, List<TransferHistoryDTO>> details;
}
