package com.example.ptweb.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidAnnounceException extends FixedAnnounceException {
    public InvalidAnnounceException(@NotNull String reason) {
        super(reason);
    }
}
