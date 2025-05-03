package com.example.ptweb.exception;

public class InvalidTorrentVersionException extends TorrentException {

    public InvalidTorrentVersionException(String reason) {
        super(reason);
    }
}
