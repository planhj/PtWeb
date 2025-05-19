package com.example.ptweb.controller.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Setter
@Getter
@Data
@Validated
public class DeadSeedResponseDTO {
    private String message;
    private List<TorrentBasicResponseDTO> torrents;

    public DeadSeedResponseDTO() {}

    public DeadSeedResponseDTO(String message, List<TorrentBasicResponseDTO> torrents) {
        this.message = message;
        this.torrents = torrents;
    }

}

