package com.example.ptweb.exception;

public class EmptyTorrentFileException extends TorrentException {

    public EmptyTorrentFileException() {
        super("Torrent files tree are empty!");
    }
}
