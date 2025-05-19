package com.example.ptweb.controller.torrent.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorrentUploadForm {
    @NotEmpty
    private String title;
    private String subtitle;
    @NotEmpty
    private String description;
    @NotEmpty
    private String category;
    private List<String> tag;
    @NotNull
    private MultipartFile file;
}
