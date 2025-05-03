package com.example.ptweb.exception;

public class AnnounceBusyException extends RetryableAnnounceException {
    public AnnounceBusyException() {
        super("Server is busy for handling announce, try again later", 30);
    }
}
