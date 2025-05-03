package com.example.ptweb.exception;

public class BadConfigException extends RuntimeException {
    public BadConfigException() {
        super("This tracker have a bad configuration in the database. Please contact the administrator.");
    }
}
