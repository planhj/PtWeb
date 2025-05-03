package com.example.ptweb.exception;

public class InvalidTorrentFileException extends TorrentException {

    public InvalidTorrentFileException(String reason) {
        super(reason);
    }
}
