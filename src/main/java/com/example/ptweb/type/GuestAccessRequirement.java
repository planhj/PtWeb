package com.example.ptweb.type;

public enum GuestAccessRequirement {
    NORMAL,
    ALREADY_LOGGED_IN,
    SPECIFIC_SECRET,
    REFERER,
    USER_AGENT,
    IP_ADDRESS,
    PASSKEY
}
